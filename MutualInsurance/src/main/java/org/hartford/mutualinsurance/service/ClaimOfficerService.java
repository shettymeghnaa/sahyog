package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.ClaimOfficer;

import java.util.List;

public interface ClaimOfficerService {
    ClaimOfficer createClaimOfficer(Long regionId, ClaimOfficer officer);
    List<ClaimOfficer> getAllClaimOfficers();
    void deactivateClaimOfficer(Long officerId);
    void reassignOfficerRegion(Long officerId, Long newRegionId);
}
