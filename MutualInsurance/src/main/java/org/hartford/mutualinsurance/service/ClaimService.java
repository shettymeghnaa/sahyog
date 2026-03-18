package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Claim;
import java.util.List;
import org.hartford.mutualinsurance.dto.ClaimRequest;

public interface ClaimService {
    Claim submitClaim(String username, ClaimRequest request);

    List<Claim> getClaimsByMember(Long memberId);

    List<Claim> getClaimsByDisaster(Long disasterId);
    
    Claim reviewClaim(Long claimId, String officerUsername);

    Claim approveClaim(Long claimId, String officerUsername, String notes);

    List<Claim> getClaimsByRegion(Long regionId);

    Claim rejectClaim(Long claimId, String officerUsername, String notes);

    Claim payClaim(Long claimId);

    List<Claim> getAllClaims();

    List<Claim> getClaimsByEmail(String email);

    void reassignClaims(Long fromOfficerId, Long toOfficerId);


    List<Claim> getPendingClaims();
    List<Claim> getPendingClaimsByOfficer(String officerEmail);
    Claim getClaimById(Long id);
}
