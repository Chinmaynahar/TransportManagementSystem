package com.TMS.ManagementService.services;


import com.TMS.ManagementService.exceptions.GenericExceptions.InsufficientCapacityException;
import com.TMS.ManagementService.exceptions.GenericExceptions.InvalidStatusTransistionException;
import com.TMS.ManagementService.exceptions.GenericExceptions.LoadAlreadyBookedException;
import com.TMS.ManagementService.exceptions.GenericExceptions.ResourceNotFoundException;
import com.TMS.ManagementService.models.dtos.requests.BookingRequestDto;
import com.TMS.ManagementService.models.dtos.responses.BookingResponseDto;
import com.TMS.ManagementService.models.entities.*;
import com.TMS.ManagementService.models.enums;
import com.TMS.ManagementService.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;
    private final TransporterTruckRepository transporterTruckRepository;

    public BookingService(BookingRepository bookingRepository, BidRepository bidRepository, LoadRepository loadRepository, TransporterRepository transporterRepository, TransporterTruckRepository transporterTruckRepository) {
        this.bookingRepository = bookingRepository;
        this.bidRepository = bidRepository;
        this.loadRepository = loadRepository;
        this.transporterRepository = transporterRepository;
        this.transporterTruckRepository = transporterTruckRepository;
    }

    @CacheEvict(
            value = {
                    "bookings",
                    "bids",
                    "loads"
            },
            allEntries = true
    )
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) throws Exception {
        Bid bid = bidRepository.findById(request.bidId())
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: "+request.bidId()));
        //Bid status validation
        if (bid.getStatus() == enums.BidStatus.ACCEPTED) {
            throw new InvalidStatusTransistionException("Bid is already accepted");
        }
        if (bid.getStatus() == enums.BidStatus.REJECTED) {
            throw new InvalidStatusTransistionException("Rejected bid cannot be accepted");
        }
        Load load = bid.getLoad();
        //load status validation
        if(!load.canAcceptBids())throw new InvalidStatusTransistionException("This load is either booked or cancelled");
        Transporter transporter = bid.getTransporter();
        //if trucks are valid
        TransporterTruck tt = transporterTruckRepository
                .findByTransporter_TransporterIdAndTruckType(transporter.getTransporterId(), load.getTruckType())
                .orElseThrow(() -> new ResourceNotFoundException("Transporter has no trucks for type:" + load.getTruckType()));
        if (transporter.getAvailableTrucks().isEmpty())throw new ResourceNotFoundException("No trucks available for this type");
       //if enough trucks are available
        if (request.allocatedTrucks() > tt.getCount()) {
            throw new InsufficientCapacityException("Transporter does not have enough trucks");
        }
        //adding trucks
        transporter.allocateTruckType(load.getTruckType(),request.allocatedTrucks());
        try {
            transporterTruckRepository.save(tt);
        } catch (Exception e) {
            throw new Exception("Some error occurred while updating trucks");
        }
        //bid acceptance
        bid.accept();
        load.allocateTrucks(request.allocatedTrucks());
        try {
            bidRepository.save(bid);
        } catch (Exception e) {
            throw new Exception("Some error occurred while updating bid");
        }
        Booking saved;
        try {
            saved = bookingRepository.save(new Booking(load, transporter, bid,load.getTruckType(), request.allocatedTrucks(), bid.getProposedRate(), enums.BookingStatus.CONFIRMED, Instant.now()));
        } catch (Exception e) {
            throw new Exception("Some error occurred while saving booking");
        }
        try{
            loadRepository.save(load);
        }catch (Exception e){
            throw new LoadAlreadyBookedException("Optimistic locking exception(Load is already booked)");
        }
        return toResponse(saved);
    }
    @Cacheable(
            value = "bookings",
            key = "#bookingId"
    )
    public BookingResponseDto getBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return toResponse(booking);
    }

    @CacheEvict(
            value = {
                    "bookings",
                    "bids",
                    "loads"
            },
            allEntries = true
    )
    @Transactional
    public BookingResponseDto cancelBooking(UUID bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        //cannot cancel booking which is already cancelled
        if (booking.getStatus() == enums.BookingStatus.CANCELLED) {
            throw new InvalidStatusTransistionException("Booking already cancelled");
        }
        Transporter transporter = booking.getTransporter();
        Load load = booking.getLoad();
        //checks trucks
        TransporterTruck tt = transporterTruckRepository
                .findByTransporter_TransporterIdAndTruckType(transporter.getTransporterId(), load.getTruckType())
                .orElseThrow(() -> new Exception("Transporter truck entry missing"));

        //restoring trucks from load
        transporter.restoreTruckType(booking.getTruckType(), booking.getAllocatedTrucks());
        load.restoreTrucks(booking.getAllocatedTrucks());
        try {
            transporterTruckRepository.save(tt);
        } catch (Exception e) {
            throw new Exception("Some error occurred while updating trucks");
        }
        //changing status
        booking.setStatus(enums.BookingStatus.CANCELLED);
        Bid bid=booking.getBid();
        bid.setStatus(enums.BidStatus.PENDING);
        load.openForBids();
        try {
            bookingRepository.save(booking);
        } catch (RuntimeException e) {
            throw new RuntimeException("Some error occurred while updating booking");
        }
        try {
            loadRepository.save(load);
        } catch (RuntimeException e) {
            throw new RuntimeException("Some error occurred while updating load");
        }
        return toResponse(booking);
    }

    //Dto mapper
    public  BookingResponseDto toResponse(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(booking.getBookingId());
        dto.setLoadId(booking.getLoad().getLoadId());
        dto.setBidId(booking.getBid().getBidId());
        dto.setTransporterId(booking.getTransporter().getTransporterId());
        dto.setTruckType(booking.getTruckType());
        dto.setAllocatedTrucks(booking.getAllocatedTrucks());
        dto.setFinalRate(booking.getFinalRate());
        dto.setStatus(booking.getStatus().name());
        dto.setBookedAt(booking.getBookedAt().toString());
        return dto;
    }
}
