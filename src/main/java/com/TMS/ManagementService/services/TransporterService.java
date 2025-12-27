package com.TMS.ManagementService.services;

import com.TMS.ManagementService.exceptions.GenericExceptions.ResourceNotFoundException;
import com.TMS.ManagementService.models.dtos.requests.TransporterRequestDto;
import com.TMS.ManagementService.models.dtos.requests.TruckCapacityDto;
import com.TMS.ManagementService.models.dtos.responses.TransporterResponseDto;
import com.TMS.ManagementService.models.entities.Transporter;
import com.TMS.ManagementService.models.entities.TransporterTruck;
import com.TMS.ManagementService.repositories.TransporterRepository;
import com.TMS.ManagementService.repositories.TransporterTruckRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransporterService {

    private final TransporterRepository transporterRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    public TransporterService(TransporterRepository transporterRepository,
                              TransporterTruckRepository transporterTruckRepository) {
        this.transporterRepository = transporterRepository;
        this.transporterTruckRepository = transporterTruckRepository;
    }


    @CacheEvict(
            value = { "transporters", "transporterTrucks" },
            allEntries = true
    )
    @Transactional
    public TransporterResponseDto register(TransporterRequestDto dto) {
        Transporter transporter = new Transporter(dto.companyName(), dto.rating());
        try {
            transporter = transporterRepository.save(transporter);
        } catch (RuntimeException e) {
            throw new RuntimeException("Some error occurred while saving transporter");
        }

        //saving in trucks repository for each different types
        for (var t : dto.availableTrucks()) {
            TransporterTruck truck = new TransporterTruck();
            truck.setTruckType(t.truckType());
            truck.setCount(t.count());
            truck.setTransporter(transporter);
            try {
                transporterTruckRepository.save(truck);
            } catch (RuntimeException e) {
                throw new RuntimeException("Some error occurred while saving truck");
            }
        }
        return toResponseDTO(transporter);
    }

    @Cacheable(
            value = "transporters",
            key = "#transporterId"
    )
    public TransporterResponseDto getTransporter(String transporterId) throws Exception {
        Transporter transporter = transporterRepository.findById(UUID.fromString(transporterId))
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found with id:"+transporterId));
        return toResponseDTO(transporter);
    }

    @CacheEvict(
            value = { "transporters", "transporterTrucks" },
            key = "#transporterId"
    )
    @Transactional
    public TransporterResponseDto updateTrucks(String transporterId, TruckCapacityDto dto) throws Exception {
        if (dto.count() < 0)
            throw new IllegalArgumentException("Truck count cannot be negative");
        Transporter transporter = transporterRepository.findById(UUID.fromString(transporterId))
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
        //checking if truckType exist so we only update count or else if its new we will save it separately
        TransporterTruck truck =
                transporterTruckRepository.findByTransporter_TransporterIdAndTruckType(transporter.getTransporterId(), dto.truckType())
                        .orElse(null);
        if (truck == null) {
            truck = new TransporterTruck();
            truck.setTruckType(dto.truckType());
            truck.setTransporter(transporter);
            truck.setCount(dto.count());
            try {
                //saving new truck type
                transporterTruckRepository.save(truck);
            } catch (RuntimeException e) {
                throw new RuntimeException("Some error occurred while updating truck");
            }
        }else{
            //updating count for type
            transporterTruckRepository.updateCount(UUID.fromString(transporterId),dto.truckType(),dto.count());
        }
        return toResponseDTO(transporter);
    }

   //DtoMapper
    private TransporterResponseDto toResponseDTO(Transporter transporter) {
        List<TruckCapacityDto> trucks =
                transporterTruckRepository.findByTransporter_TransporterId(transporter.getTransporterId())
                        .stream()
                        .map(t -> new TruckCapacityDto(t.getTruckType(), t.getCount()))
                        .collect(Collectors.toList());
        return new TransporterResponseDto(
                transporter.getTransporterId(),
                transporter.getCompanyName(),
                transporter.getRating(),
                trucks
        );
    }
}

