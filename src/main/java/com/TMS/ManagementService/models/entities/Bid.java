package com.TMS.ManagementService.models.entities;

import com.TMS.ManagementService.models.enums;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bids", indexes = {
        @Index(name = "idx_bid_load", columnList = "load_id"),
        @Index(name = "idx_bid_transporter", columnList = "transporter_id"),
        @Index(name = "idx_bid_status", columnList = "status")
})
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bid_id")
    private UUID bidId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "load_id", nullable = false,referencedColumnName = "load_id")
    private Load load;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter_id", nullable = false,referencedColumnName = "transporter_id")
    private Transporter transporter;

    @Column(nullable = false)
    private double proposedRate;

    @Column(nullable = false)
    private int trucksOffered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private enums.BidStatus status;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    public UUID getBidId() {
        return bidId;
    }

    public void setBidId(UUID bidId) {
        this.bidId = bidId;
    }

    public Bid() {
    }

    public Load getLoad() {
        return load;
    }

    public void setLoad(Load load) {
        this.load = load;
    }

    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public double getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(double proposedRate) {
        this.proposedRate = proposedRate;
    }

    public int getTrucksOffered() {
        return trucksOffered;
    }

    public void setTrucksOffered(int trucksOffered) {
        this.trucksOffered = trucksOffered;
    }

    public enums.BidStatus getStatus() {
        return status;
    }

    public void setStatus(enums.BidStatus status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void accept() { this.status = enums.BidStatus.ACCEPTED; }

    public void reject() { this.status = enums.BidStatus.REJECTED; }

    public Bid(Load load, Transporter transporter, double proposedRate, int trucksOffered, enums.BidStatus status, Instant submittedAt) {
        this.load = load;
        this.transporter = transporter;
        this.proposedRate = proposedRate;
        this.trucksOffered = trucksOffered;
        this.status = status;
        this.submittedAt = submittedAt;
    }
}

