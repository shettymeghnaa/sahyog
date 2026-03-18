package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.DisasterEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisasterRepository extends JpaRepository<DisasterEvent, Long> {
    List<DisasterEvent> findByRegionId(Long regionId);
    List<DisasterEvent> findByStatus(String status);
    List<DisasterEvent> findByRegionIdAndStatus(Long regionId, String status);
    boolean existsByRegionIdAndStatus(Long regionId, String status);
}

