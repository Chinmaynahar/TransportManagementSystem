package com.TMS.ManagementService.models.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transporters", indexes = {
        @Index(name = "idx_transporter_rating", columnList = "rating")
})
public class Transporter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transporter_id")
    private UUID transporterId;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private double rating;

    @Version
    private Long version;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transporter_id")
    private List<TransporterTruck> availableTrucks = new ArrayList<>();

    public Transporter() {
    }

    public UUID getTransporterId() {
        return transporterId;
    }

    public Transporter(UUID transporterId, String companyName, double rating, Long version, List<TransporterTruck> availableTrucks) {
        this.transporterId = transporterId;
        this.companyName = companyName;
        this.rating = rating;
        this.version = version;
        this.availableTrucks = availableTrucks;
    }

    public Transporter(String companyName, double rating) {
        this.companyName = companyName;
        this.rating = rating;
    }

    //removing trucks from transporters count to add to load
    public void allocateTruckType(String truckType, int count) {
        if (count <= 0) throw new IllegalArgumentException("Truck count must be positive.");
        TransporterTruck truck = availableTrucks.stream()
                .filter(t -> t.getTruckType().equals(truckType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Truck type not found: " + truckType));

        if (truck.getCount() < count)
            throw new IllegalStateException("Not enough trucks available for type " + truckType);
        truck.setCount(truck.getCount() - count);
    }

   //restoring trucks in case of booking cancellation
    public void restoreTruckType(String truckType, int count) {
        if (count <= 0) throw new IllegalArgumentException("Truck count must be positive.");

        TransporterTruck truck = availableTrucks.stream()
                .filter(t -> t.getTruckType().equals(truckType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Truck type not found: " + truckType));
        truck.setCount(truck.getCount() + count);
    }
   //checks available total trucks
    public int totalAvailableTrucks() {
        return availableTrucks.stream()
                .mapToInt(TransporterTruck::getCount)
                .sum();
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<TransporterTruck> getAvailableTrucks() {
        return availableTrucks;
    }

    public void setAvailableTrucks(List<TransporterTruck> availableTrucks) {
        this.availableTrucks = availableTrucks;
    }

}
