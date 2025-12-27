package com.TMS.ManagementService.repositories;

import com.TMS.ManagementService.models.entities.Bid;
import com.TMS.ManagementService.models.enums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    @Query("SELECT b FROM Bid b WHERE b.load.loadId = :loadId AND b.status <> 'REJECTED'")
    List<Bid> findActiveBids(@Param("loadId") UUID loadId);

    List<Bid> findByLoad_LoadId(UUID loadId);

    List<Bid> findByTransporter_TransporterId(UUID transporterId);

    List<Bid> findByLoad_LoadIdAndStatus(UUID loadId, enums.BidStatus status);

    List<Bid> findByLoad_LoadIdAndTransporter_TransporterIdAndStatus(
            UUID loadId, UUID transporterId, enums.BidStatus status);
}
