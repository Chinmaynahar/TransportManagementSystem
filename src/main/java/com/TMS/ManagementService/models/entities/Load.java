package com.TMS.ManagementService.models.entities;

import com.TMS.ManagementService.exceptions.GenericExceptions.InvalidStatusTransistionException;
import com.TMS.ManagementService.models.enums;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loads", indexes = {
        @Index(name = "idx_load_shipperid", columnList = "shipperId"),
        @Index(name = "idx_load_status", columnList = "status"),
        @Index(name = "idx_load_loading_date", columnList = "loadingDate") // Added for performance
})
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "load_id")
    private UUID loadId;

    @Column(nullable = false)
    private String shipperId;

    @Column(nullable = false)
    private String loadingCity;

    @Column(nullable = false)
    private String unloadingCity;

    @Column(nullable = false)
    private Instant loadingDate;

    @Column(nullable = false)
    private String productType;

    @Column(nullable = false)
    private double weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private enums.WeightUnit weightUnit;

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int noOfTrucks;

    @Column(nullable = false)
    private int remainingTrucks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private enums.LoadStatus status;

    @Column(nullable = false, updatable = false)
    private Instant datePosted;

    @Version
    private Long version;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();


    public Load() {
    }


    public Load(String shipperId, String loadingCity, String unloadingCity, Instant loadingDate,
                String productType, double weight, enums.WeightUnit weightUnit,
                String truckType, int noOfTrucks) {
        this.shipperId = shipperId;
        this.loadingCity = loadingCity;
        this.unloadingCity = unloadingCity;
        this.loadingDate = loadingDate;
        this.productType = productType;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.remainingTrucks = noOfTrucks;
        this.status = enums.LoadStatus.POSTED;
        this.datePosted = Instant.now();
    }
    //testing purpose
    public Load(String goodsA, String flatbed, double v, enums.LoadStatus loadStatus) {
    }

    // Changes load state so bids can be put
    public void openForBids() {
        if (this.status == enums.LoadStatus.POSTED) {
            this.status = enums.LoadStatus.OPEN_FOR_BIDS;
        }
    }

    //checks load status for accepting bids
    public boolean canAcceptBids() {
        return this.status == enums.LoadStatus.POSTED ||
                this.status == enums.LoadStatus.OPEN_FOR_BIDS;
    }

    //checks load status for cancellation
    public boolean canBeCancelled() {
        return this.status != enums.LoadStatus.BOOKED &&
                this.status != enums.LoadStatus.CANCELLED;
    }

   //cancel load
    public void cancel() {
        if (!canBeCancelled()) {
            throw new InvalidStatusTransistionException("Cannot cancel load in status: " + this.status);
        }
        this.status = enums.LoadStatus.CANCELLED;
    }

    //allocating trucks to load
    public void allocateTrucks(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        if (remainingTrucks < count) {
            throw new IllegalStateException(
                    "Not enough trucks remaining. Available: " + remainingTrucks + ", Requested: " + count
            );
        }
        remainingTrucks -= count;
        if (remainingTrucks == 0) {
            this.status = enums.LoadStatus.BOOKED;
        } else if (this.status == enums.LoadStatus.POSTED) {
            // Transition to OPEN_FOR_BIDS if first allocation
            this.status = enums.LoadStatus.OPEN_FOR_BIDS;
        }
    }
    //restoring trucks from load
    public void restoreTrucks(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        remainingTrucks += count;

        if (this.status == enums.LoadStatus.BOOKED && remainingTrucks > 0) {
            this.status = enums.LoadStatus.OPEN_FOR_BIDS;
        }
    }

    //adding bid to load
    public void addBid(Bid bid) {
        bids.add(bid);
        bid.setLoad(this);

        // Transition to OPEN_FOR_BIDS when first bid is received
        if (this.status == enums.LoadStatus.POSTED && !bids.isEmpty()) {
            this.status = enums.LoadStatus.OPEN_FOR_BIDS;
        }
    }
    //add booking to load
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setLoad(this);
    }


    public UUID getLoadId() {
        return loadId;
    }

    public String getShipperId() {
        return shipperId;
    }

    public String getLoadingCity() {
        return loadingCity;
    }

    public String getUnloadingCity() {
        return unloadingCity;
    }

    public Instant getLoadingDate() {
        return loadingDate;
    }

    public String getProductType() {
        return productType;
    }

    public double getWeight() {
        return weight;
    }

    public enums.WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public String getTruckType() {
        return truckType;
    }

    public int getNoOfTrucks() {
        return noOfTrucks;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }

    public void setLoadingCity(String loadingCity) {
        this.loadingCity = loadingCity;
    }

    public void setUnloadingCity(String unloadingCity) {
        this.unloadingCity = unloadingCity;
    }

    public void setLoadingDate(Instant loadingDate) {
        this.loadingDate = loadingDate;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setWeightUnit(enums.WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public void setNoOfTrucks(int noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }

    public void setRemainingTrucks(int remainingTrucks) {
        this.remainingTrucks = remainingTrucks;
    }

    public void setStatus(enums.LoadStatus status) {
        this.status = status;
    }

    public void setDatePosted(Instant datePosted) {
        this.datePosted = datePosted;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public int getRemainingTrucks() {
        return remainingTrucks;
    }

    public enums.LoadStatus getStatus() {
        return status;
    }

    public Instant getDatePosted() {
        return datePosted;
    }
}