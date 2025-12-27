package com.TMS.ManagementService.models.entities;

import com.TMS.ManagementService.models.dtos.requests.TruckCapacityDto;
import jakarta.persistence.*;

@Entity
@Table(name = "transporter_trucks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"transporter_id", "truckType"})
})
public class TransporterTruck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false,referencedColumnName = "transporter_id")
    private Transporter transporter;

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int count;
    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransporterTruck() {
    }

    public TransporterTruck(Long id, String truckType, int count) {
        this.id = id;
        this.truckType = truckType;
        this.count = count;
    }

    public TransporterTruck(String truckType, int count) {
        this.truckType = truckType;
        this.count = count;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

