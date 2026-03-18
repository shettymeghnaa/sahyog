package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.DisasterEvent;

import java.util.List;

public interface DisasterService {
    DisasterEvent declareDisaster(Long regionId, DisasterEvent disaster);

    List<DisasterEvent> getDisastersByRegion(Long regionId);

    DisasterEvent closeDisaster(Long disasterId);
    List<DisasterEvent> getAllDisasters();
    List<DisasterEvent> getActiveDisasters();
    List<DisasterEvent> getActiveDisastersByRegion(Long regionId);
}
