package com.TMS.ManagementService.repositories;

import com.TMS.ManagementService.models.entities.TransporterTruck;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransporterTruckRepository extends JpaRepository<TransporterTruck,Long> {
    List<TransporterTruck> findByTransporter_TransporterId(UUID transporterId);

    Optional<TransporterTruck> findByTransporter_TransporterIdAndTruckType(UUID transporterId, String truckType);

    @Modifying
    @Transactional
    @Query("Update TransporterTruck t SET t.count= :count WHERE t.transporter.transporterId= :transporterId AND t.truckType= :truckType")
    int updateCount(UUID transporterId,String truckType,int count);
}
