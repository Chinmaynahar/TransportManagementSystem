package com.TMS.ManagementService.models.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto implements Serializable {
   private UUID bookingId;
   private UUID loadId;
   private UUID bidId;
   private UUID transporterId;
   private String truckType;
   private Integer allocatedTrucks;
   private Double finalRate;

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    private String status;
   private String bookedAt;

    public BookingResponseDto(UUID bookingId, UUID loadId, UUID bidId, UUID transporterId,String truckType, Integer allocatedTrucks, Double finalRate, String status, String bookedAt) {
        this.bookingId = bookingId;
        this.loadId = loadId;
        this.bidId = bidId;
        this.transporterId = transporterId;
        this.truckType=truckType;
        this.allocatedTrucks = allocatedTrucks;
        this.finalRate = finalRate;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public BookingResponseDto(UUID bookingId, String status) {
        this.bookingId = bookingId;
        this.status = status;
    }

    public BookingResponseDto() {

    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getLoadId() {
        return loadId;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public UUID getBidId() {
        return bidId;
    }

    public void setBidId(UUID bidId) {
        this.bidId = bidId;
    }

    public UUID getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(UUID transporterId) {
        this.transporterId = transporterId;
    }

    public Integer getAllocatedTrucks() {
        return allocatedTrucks;
    }

    public void setAllocatedTrucks(int allocatedTrucks) {
        this.allocatedTrucks = allocatedTrucks;
    }

    public Double getFinalRate() {
        return finalRate;
    }

    public void setFinalRate(double finalRate) {
        this.finalRate = finalRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(String bookedAt) {
        this.bookedAt = bookedAt;
    }

}
