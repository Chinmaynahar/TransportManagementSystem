package com.TMS.ManagementService.models.entities;

import com.TMS.ManagementService.models.enums;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_load", columnList = "load_id"),
        @Index(name = "idx_booking_transporter", columnList = "transporter_id")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "load_id", nullable = false, referencedColumnName = "load_id")
    private Load load;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bid_id", nullable = false ,referencedColumnName = "bid_id")
    private Bid bid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter_id", nullable = false,referencedColumnName = "transporter_id")
    private Transporter transporter;

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int allocatedTrucks;

    @Column(nullable = false)
    private double finalRate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private enums.BookingStatus status;

    @Column(nullable = false)
    private Instant bookedAt;


    public UUID getBookingId() {
        return bookingId;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public Booking() {}

    public Booking(Load load, Transporter transporter,Bid bid,String truckType, int allocatedTrucks, double finalRate, enums.BookingStatus status, Instant bookedAt) {
        this.load = load;
        this.bid=bid;
        this.transporter = transporter;
        this.truckType=truckType;
        this.allocatedTrucks = allocatedTrucks;
        this.finalRate = finalRate;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public Booking(UUID bookingId, Load load, Bid bid, Transporter transporter, int allocatedTrucks, double finalRate, enums.BookingStatus status, Instant bookedAt) {
        this.bookingId = bookingId;
        this.load = load;
        this.bid = bid;
        this.transporter = transporter;
        this.allocatedTrucks = allocatedTrucks;
        this.finalRate = finalRate;
        this.status = status;
        this.bookedAt = bookedAt;
    }
   //changes booking state to confirmation with time of confirmation
    public void confirmBooking() {
        this.status = enums.BookingStatus.CONFIRMED;
        this.bookedAt = Instant.now();
    }

    public void completeBooking() {
        this.status = enums.BookingStatus.COMPLETED;
    }

    public void cancelBooking() {
        this.status = enums.BookingStatus.CANCELLED;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public Load getLoad() {
        return load;
    }

    public void setLoad(Load load) {
        this.load = load;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public int getAllocatedTrucks() {
        return allocatedTrucks;
    }

    public void setAllocatedTrucks(int allocatedTrucks) {
        this.allocatedTrucks = allocatedTrucks;
    }

    public double getFinalRate() {
        return finalRate;
    }

    public void setFinalRate(double finalRate) {
        this.finalRate = finalRate;
    }

    public enums.BookingStatus getStatus() {
        return status;
    }

    public void setStatus(enums.BookingStatus status) {
        this.status = status;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(Instant bookedAt) {
        this.bookedAt = bookedAt;
    }


}

