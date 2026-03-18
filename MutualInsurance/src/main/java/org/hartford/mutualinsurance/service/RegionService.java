package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Region;

import java.util.List;

public interface RegionService {
    Region createRegion(Region region);

    List<Region> getAllRegions();

    Region getRegionById(Long id);

    Region updateRegion(Long id, Region updatedRegion);

    Region updateRiskLevel(Long id, String riskLevel);

    void deleteRegion(Long id);
}
