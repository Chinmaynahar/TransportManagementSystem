package com.TMS.ManagementService.models.dtos.responses;

import com.TMS.ManagementService.models.entities.Bid;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidResponseDto implements Serializable {
   private UUID bidId;
   private UUID loadId;
   private UUID transporterId;
   private Double proposedRate;
   private Integer trucksOffered;
   private String status;
   private String submittedAt;
   private Double transporterRating;
   private Double score;

    public Double getTransporterRating() {
        return transporterRating;
    }

    public void setTransporterRating(double transporterRating) {
        this.transporterRating = transporterRating;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public BidResponseDto(UUID bidId, UUID loadId, UUID transporterId, Double proposedRate, Integer trucksOffered, String status, String submittedAt) {
        this.bidId = bidId;
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.trucksOffered = trucksOffered;
        this.status = status;
        this.submittedAt = submittedAt;
    }


    public UUID getBidId() {
        return bidId;
    }

    public void setBidId(UUID bidId) {
        this.bidId = bidId;
    }

    public UUID getLoadId() {
        return loadId;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public UUID getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(UUID transporterId) {
        this.transporterId = transporterId;
    }

    public BidResponseDto(UUID bidId, String status) {
        this.bidId = bidId;
        this.status = status;
    }

    public Double getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(double proposedRate) {
        this.proposedRate = proposedRate;
    }

    public Integer getTrucksOffered() {
        return trucksOffered;
    }

    public void setTrucksOffered(int trucksOffered) {
        this.trucksOffered = trucksOffered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public BidResponseDto() {
    }

    public BidResponseDto(UUID bidId, UUID transporterId, Double proposedRate, Integer trucksOffered, String status) {
        this.bidId = bidId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.trucksOffered = trucksOffered;
        this.status = status;
    }

    //DTO mappers
    public static BidResponseDto from(Bid bid){
        BidResponseDto dto = new BidResponseDto();
        dto.setBidId(bid.getBidId());
        dto.setLoadId(bid.getLoad().getLoadId());
        dto.setTransporterId(bid.getTransporter().getTransporterId());
        dto.setProposedRate(bid.getProposedRate());
        dto.setTrucksOffered(bid.getTrucksOffered());
        dto.setStatus(bid.getStatus().name());
        dto.setSubmittedAt(bid.getSubmittedAt().toString());
        return dto;
    }

    public static BidResponseDto from(Bid bid, Double rating, Double score) {
        BidResponseDto dto = from(bid);
        dto.setTransporterRating(rating);
        dto.setScore(score);
        return dto;
    }
}
