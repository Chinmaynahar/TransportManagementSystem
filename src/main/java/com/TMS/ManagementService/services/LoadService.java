package com.TMS.ManagementService.services;

import com.TMS.ManagementService.exceptions.GenericExceptions.InvalidStatusTransistionException;
import com.TMS.ManagementService.exceptions.GenericExceptions.ResourceNotFoundException;
import com.TMS.ManagementService.models.dtos.requests.LoadRequestDto;
import com.TMS.ManagementService.models.dtos.responses.BidResponseDto;
import com.TMS.ManagementService.models.dtos.responses.LoadResponseDto;
import com.TMS.ManagementService.models.entities.*;
import com.TMS.ManagementService.models.enums;
import com.TMS.ManagementService.repositories.BidRepository;
import com.TMS.ManagementService.repositories.LoadRepository;
import com.TMS.ManagementService.repositories.TransporterRepository;
import com.TMS.ManagementService.repositories.TransporterTruckRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LoadService {

    private final LoadRepository loadRepository;
    private final BidRepository bidRepository;
    private final TransporterRepository transporterRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    public LoadService(
            LoadRepository loadRepository,
            BidRepository bidRepository,
            TransporterRepository transporterRepository, TransporterTruckRepository transporterTruckRepository
    ) {
        this.loadRepository = loadRepository;
        this.bidRepository = bidRepository;
        this.transporterRepository = transporterRepository;
        this.transporterTruckRepository = transporterTruckRepository;
    }

    //Register Load
    @CacheEvict(
            value = { "loads" },
            allEntries = true
    )
    @Transactional
    public LoadResponseDto register(LoadRequestDto dto) {

        Load load = new Load();
        load.setShipperId(dto.shipperId());
        load.setLoadingCity(dto.loadingCity());
        load.setUnloadingCity(dto.unloadingCity());
        load.setLoadingDate(Instant.parse(dto.loadingDate()));
        load.setProductType(dto.productType());
        load.setWeight(dto.weight());
        load.setWeightUnit(enums.WeightUnit.valueOf(dto.weightUnit()));
        load.setTruckType(dto.truckType());
        load.setNoOfTrucks(dto.noOfTrucks());
        load.setRemainingTrucks(dto.noOfTrucks());
        load.setStatus(enums.LoadStatus.POSTED);
        load.setDatePosted(Instant.now());
        Load saved;
        try {
           saved = loadRepository.save(load);
        } catch (RuntimeException e) {
            throw new RuntimeException("Some error occurred while saving load");
        }
        return new LoadResponseDto(saved.getLoadId(), saved.getStatus().toString());
    }



    public List<LoadResponseDto> getLoads(String shipperId, String status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Load> result;
       try{
        if (shipperId != null && status != null) {

                result = loadRepository.findByShipperIdAndStatus(
                        shipperId,
                        enums.LoadStatus.valueOf(status),
                        pageable
                );
        } else if (shipperId != null) {

                result = loadRepository.findByShipperId(shipperId, pageable);

        } else if (status != null) {

                result = loadRepository.findByStatus(
                        enums.LoadStatus.valueOf(status),
                        pageable
                );
        } else {
            result = loadRepository.findAll(pageable);
        }} catch (RuntimeException e) {
           throw new ResourceNotFoundException("Load not found");
       }
        return result.stream()
                .map(LoadResponseDto::from)
                .toList();
    }

    // Cancel load
    @CacheEvict(
            value = { "loads", "bestBids", "loadWithBids" },
            key = "#loadId",
            allEntries = true
    )
    @Transactional
    public LoadResponseDto cancelLoad(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: "+loadId));

        if (!load.canBeCancelled()) {
            throw new InvalidStatusTransistionException(" Cannot cancel load in status: " + load.getStatus());
        }
        //restoring trucks from each booking for this load
        for (Booking booking : load.getBookings()) {
            Bid bid = booking.getBid();
            if (bid.getStatus()== enums.BidStatus.REJECTED)continue;//Will not change status of rejected bids
            bid.setStatus(enums.BidStatus.PENDING);
            bidRepository.save(bid);
            //trucks restoration
            Transporter transporter = booking.getTransporter();
            transporter.restoreTruckType(
                    booking.getTruckType(),
                    booking.getAllocatedTrucks()
            );
        }
        //load status cancellation
        load.cancel();
        try {
            loadRepository.save(load);
        } catch (RuntimeException e) {
            throw new RuntimeException("Some error occurred while updating load");
        }
        return new LoadResponseDto(loadId, load.getStatus().toString());
    }

    @Cacheable(
            value = "bestBids",
            key = "#loadId",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<BidResponseDto> getBestBids(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: "+loadId));

        List<Bid> bids = bidRepository.findByLoad_LoadIdAndStatus(
                loadId,
                enums.BidStatus.PENDING
        );
        return bids.stream()
                .map(bid -> {
                    Transporter transporter = transporterRepository.findById(
                                    bid.getTransporter().getTransporterId()
                            )
                            .orElseThrow(() -> new ResourceNotFoundException("Transporter not found with id: "+ bid.getTransporter().getTransporterId()));

                    double score = calculateScore(bid.getProposedRate(), transporter.getRating());//score calculation
                    return BidResponseDto.from(bid, transporter.getRating(), score);
                })
                .sorted(Comparator.comparingDouble(BidResponseDto::getScore).reversed())//sorting
                .toList();
    }
    @Cacheable(
            value = "loadWithBids",
            key = "#loadId"

    )
    public LoadResponseDto getLoadWithBids(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Load not found with id:"+ loadId));

        List<Bid> activeBids = bidRepository.findActiveBids(loadId);

        return LoadResponseDto.from(load, activeBids);
    }
    //score calculation
    private double calculateScore(double rate, double rating) {
        return ((1 / rate) * 0.7) + ((rating / 5.0) * 0.3);
    }
}
