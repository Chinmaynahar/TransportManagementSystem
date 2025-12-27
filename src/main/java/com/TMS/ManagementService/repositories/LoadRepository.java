package com.TMS.ManagementService.repositories;

import com.TMS.ManagementService.models.entities.Load;

import com.TMS.ManagementService.models.enums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {


    Page<Load> findByShipperId(String shipperId, Pageable pageable);

    Page<Load> findByStatus(enums.LoadStatus status, Pageable pageable);

    Page<Load> findByShipperIdAndStatus(String shipperId, enums.LoadStatus status, Pageable pageable);

}
