package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.DisasterEvent;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.DisasterRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisasterServiceImpl implements DisasterService {
    private final DisasterRepository disasterRepository;
    private final RegionRepository regionRepository;

    public DisasterServiceImpl(DisasterRepository disasterRepository,
                               RegionRepository regionRepository) {
        this.disasterRepository = disasterRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public DisasterEvent declareDisaster(Long regionId, DisasterEvent disaster) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));

        // Block if there is already an active disaster in this region
        if (disasterRepository.existsByRegionIdAndStatus(regionId, "ACTIVE")) {
            throw new InvalidOperationException(
                "An active disaster already exists for this region. Close the existing disaster before declaring a new one."
            );
        }

        disaster.setRegion(region);
        disaster.setStatus("ACTIVE");
        
        if (disaster.getStartDate() == null) {
            disaster.setStartDate(java.time.LocalDate.now());
        }

        return disasterRepository.save(disaster);
    }

    @Override
    public List<DisasterEvent> getDisastersByRegion(Long regionId) {
        return disasterRepository.findByRegionId(regionId);
    }

    @Override
    public DisasterEvent closeDisaster(Long disasterId) {
        DisasterEvent disaster = disasterRepository.findById(disasterId)
                .orElseThrow(() -> new ResourceNotFoundException("Disaster not found"));

        if (!disaster.getStatus().equals("ACTIVE")) {
            throw new InvalidOperationException("Disaster already closed");
        }

        disaster.setStatus("CLOSED");

        return disasterRepository.save(disaster);
    }

    @Override
    public List<DisasterEvent> getAllDisasters() {
        return disasterRepository.findAll();
    }

    /** Returns only disasters with ACTIVE status — for member-facing endpoints. */
    @Override
    public List<DisasterEvent> getActiveDisasters() {
        return disasterRepository.findByStatus("ACTIVE");
    }

    /** Returns ACTIVE disasters within a specific region — for member claim submission. */
    @Override
    public List<DisasterEvent> getActiveDisastersByRegion(Long regionId) {
        return disasterRepository.findByRegionIdAndStatus(regionId, "ACTIVE");
    }
}
