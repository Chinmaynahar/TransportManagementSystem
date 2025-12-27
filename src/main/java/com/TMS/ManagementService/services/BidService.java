package com.TMS.ManagementService.services;


import com.TMS.ManagementService.exceptions.GenericExceptions.InsufficientCapacityException;
import com.TMS.ManagementService.exceptions.GenericExceptions.InvalidStatusTransistionException;
import com.TMS.ManagementService.exceptions.GenericExceptions.ResourceNotFoundException;
import com.TMS.ManagementService.models.dtos.requests.BidRequestDto;
import com.TMS.ManagementService.models.dtos.responses.BidResponseDto;
import com.TMS.ManagementService.models.entities.Bid;
import com.TMS.ManagementService.models.entities.Load;
import com.TMS.ManagementService.models.entities.Transporter;
import com.TMS.ManagementService.models.entities.TransporterTruck;
import com.TMS.ManagementService.models.enums;
import com.TMS.ManagementService.repositories.BidRepository;
import com.TMS.ManagementService.repositories.LoadRepository;
import com.TMS.ManagementService.repositories.TransporterRepository;
import com.TMS.ManagementService.repositories.TransporterTruckRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    public BidService(BidRepository bidRepository, LoadRepository loadRepository, TransporterRepository transporterRepository, TransporterTruckRepository transporterTruckRepository) {
        this.bidRepository = bidRepository;
        this.loadRepository = loadRepository;
        this.transporterRepository = transporterRepository;
        this.transporterTruckRepository = transporterTruckRepository;
    }

    @CacheEvict(
            value = {
                    "bids",
                    "bestBids",
                    "loadWithBids"
            },
            key = "#bidRequestDto.loadId()",
            allEntries = true
    )
    @Transactional
    public BidResponseDto register(BidRequestDto bidRequestDto) throws Exception {
        Load load=loadRepository.findById(bidRequestDto.loadId())
                .orElseThrow(()->new ResourceNotFoundException("Load not found with id :"+bidRequestDto.loadId()));
        Transporter transporter=transporterRepository.findById(bidRequestDto.transporterId())
                .orElseThrow(()->new ResourceNotFoundException("Transporter not found with id: "+bidRequestDto.transporterId()));
        //load status validation
        if (load.getStatus() == enums.LoadStatus.CANCELLED || load.getStatus() == enums.LoadStatus.BOOKED) {
            throw new InvalidStatusTransistionException("Cannot Bid on cancelled or booked loads");
        }

        TransporterTruck truck = transporterTruckRepository
                .findByTransporter_TransporterIdAndTruckType(transporter.getTransporterId(), load.getTruckType())
                .orElseThrow(() -> new ResourceNotFoundException("Truck type not found"));
        //enough trucks available?
        if (bidRequestDto.trucksOffered() > truck.getCount()) {
            throw new InsufficientCapacityException("Not enough trucks available");
        }
        Bid saved;
        //changing load status
        load.openForBids();
        try {
            saved = bidRepository.save(new Bid(load, transporter, bidRequestDto.proposedRate(), bidRequestDto.trucksOffered(), enums.BidStatus.PENDING, Instant.now()));
        }catch (Exception e){
            throw new Exception("Some Error occurred while saving bid");
        }
        load.addBid(saved);
        try {
            loadRepository.save(load);
        }catch (Exception e){
            throw new Exception("Some error occurred while updating load");
        }
        return toResponse(saved);
    }

    public List<BidResponseDto> filterBids(UUID loadId, UUID transporterId, enums.BidStatus status) throws Exception{
        List<Bid> result;
        if (loadId != null && transporterId != null && status != null) {
            try {
                result = bidRepository.findByLoad_LoadIdAndTransporter_TransporterIdAndStatus(loadId, transporterId, status);
            } catch (Exception e) {
                throw new ResourceNotFoundException(e.getMessage());
            }
        } else if (loadId != null && status != null) {
            try {
                result = bidRepository.findByLoad_LoadIdAndStatus(loadId, status);
            } catch (Exception e) {
                throw new ResourceNotFoundException(e.getMessage());
            }
        } else if (loadId != null) {
            try{
                result = bidRepository.findByLoad_LoadId(loadId);
            }catch (Exception e) {
                throw new ResourceNotFoundException(e.getMessage());
            }
        } else if (transporterId != null) {
            try {
                result = bidRepository.findByTransporter_TransporterId(transporterId);
            }catch (Exception e) {
                throw new ResourceNotFoundException(e.getMessage());
            }
        } else {
            try {
                result = bidRepository.findAll();
            }catch (Exception e) {
                throw new ResourceNotFoundException(e.getMessage());
            }
        }
        if (result.isEmpty())throw new ResourceNotFoundException("No bids were found");
        return result.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Cacheable(
            value = "bids",
            key = "#bidId"
    )
    public BidResponseDto getBid(UUID bidId) throws Exception {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        return toResponse(bid);
    }


    @CacheEvict(
            value = {
                    "bids",
                    "bestBids",
                    "loadWithBids"
            },
            allEntries = true
    )
    @Transactional
    public BidResponseDto rejectBid(UUID bidId) throws Exception {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        //bid status validation
        if (bid.getStatus() != enums.BidStatus.PENDING) {
            throw new InvalidStatusTransistionException("Only pending bids can be rejected");
        }
        bid.reject();
        bidRepository.save(bid);
        return toResponse(bid);
    }

    //Dto mapper
    private BidResponseDto toResponse(Bid bid) {
        BidResponseDto res = new BidResponseDto();
        res.setBidId(bid.getBidId());
        res.setLoadId(bid.getLoad().getLoadId());
        res.setTransporterId(bid.getTransporter().getTransporterId());
        res.setProposedRate(bid.getProposedRate());
        res.setTrucksOffered(bid.getTrucksOffered());
        res.setStatus(bid.getStatus().name());
        res.setSubmittedAt(bid.getSubmittedAt().toString());
        return res;
    }
}
