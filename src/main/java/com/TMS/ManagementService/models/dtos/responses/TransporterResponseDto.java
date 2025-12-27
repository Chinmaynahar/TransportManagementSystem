package com.TMS.ManagementService.models.dtos.responses;

import com.TMS.ManagementService.models.dtos.requests.TruckCapacityDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransporterResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID transporterId;
    private String companyName;
    private Double rating;
    private List<TruckCapacityDto> availableTrucks;

    public UUID getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(UUID transporterId) {
        this.transporterId = transporterId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public TransporterResponseDto(UUID transporterId, String companyName, double rating, List<TruckCapacityDto> availableTrucks) {
        this.transporterId = transporterId;
        this.companyName = companyName;
        this.rating = rating;
        this.availableTrucks = availableTrucks;
    }
    public TransporterResponseDto() {
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<TruckCapacityDto> getAvailableTrucks() {
        return availableTrucks;
    }

    public void setAvailableTrucks(List<TruckCapacityDto> availableTrucks) {
        this.availableTrucks = availableTrucks;
    }

}
