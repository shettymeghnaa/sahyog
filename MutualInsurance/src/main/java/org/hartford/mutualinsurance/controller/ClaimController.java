package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.dto.ClaimRequest;
import org.hartford.mutualinsurance.dto.ClaimResponse;
import org.hartford.mutualinsurance.dto.VerificationRequest;
import org.hartford.mutualinsurance.entity.Claim;
import org.hartford.mutualinsurance.service.ClaimService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // -------------------------------------------------------
    // MEMBER ENDPOINTS
    // -------------------------------------------------------

    /** Submit a new claim. Member resolved from JWT — no memberId in body. */
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/submit")
    public ClaimResponse submitClaim(@RequestBody ClaimRequest request,
                                     Authentication authentication) {
        Claim claim = claimService.submitClaim(
                authentication.getName(),
                request
        );
        return ClaimResponse.from(claim);
    }

    /** Member views their own claims. */
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my")
    public List<ClaimResponse> getMyClaims(Authentication authentication) {
        return claimService.getClaimsByEmail(authentication.getName())
                .stream()
                .map(ClaimResponse::from)
                .toList();
    }

    // -------------------------------------------------------
    // CLAIM OFFICER ENDPOINTS
    // -------------------------------------------------------

    /** All PENDING claims assigned to the current officer. */
    @PreAuthorize("hasRole('CLAIM_OFFICER')")
    @GetMapping("/pending")
    public List<ClaimResponse> getPendingClaims(Authentication authentication) {
        return claimService.getPendingClaimsByOfficer(authentication.getName())
                .stream()
                .map(ClaimResponse::from)
                .toList();
    }

    /** Get any claim by ID (Officer/Admin only). */
    @PreAuthorize("hasAnyRole('CLAIM_OFFICER', 'ADMIN')")
    @GetMapping("/{id}")
    public ClaimResponse getClaimById(@PathVariable("id") Long id) {
        return ClaimResponse.from(claimService.getClaimById(id));
    }

    /** Move a claim into UNDER_REVIEW state and record officer. */
    @PreAuthorize("hasRole('CLAIM_OFFICER')")
    @PostMapping("/review/{claimId}")
    public ClaimResponse reviewClaim(@PathVariable("claimId") Long claimId,
                                     Authentication authentication) {
        Claim claim = claimService.reviewClaim(claimId, authentication.getName());
        return ClaimResponse.from(claim);
    }

    /** Approve a claim. Officer username recorded for audit. */
    @PreAuthorize("hasRole('CLAIM_OFFICER')")
    @PostMapping("/approve/{claimId}")
    public ClaimResponse approveClaim(@PathVariable("claimId") Long claimId,
                                      @RequestBody VerificationRequest request,
                                      Authentication authentication) {
        Claim claim = claimService.approveClaim(
            claimId, 
            authentication.getName(), 
            request.getVerificationNotes()
        );
        return ClaimResponse.from(claim);
    }

    /** Reject a claim. Officer username recorded for audit. */
    @PreAuthorize("hasRole('CLAIM_OFFICER')")
    @PostMapping("/reject/{claimId}")
    public ClaimResponse rejectClaim(@PathVariable("claimId") Long claimId,
                                     @RequestBody VerificationRequest request,
                                     Authentication authentication) {
        Claim claim = claimService.rejectClaim(
            claimId, 
            authentication.getName(), 
            request.getVerificationNotes()
        );
        return ClaimResponse.from(claim);
    }

    // -------------------------------------------------------
    // ADMIN ENDPOINTS — read-only views
    // -------------------------------------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ClaimResponse> getAllClaims() {
        return claimService.getAllClaims().stream().map(ClaimResponse::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}")
    public List<ClaimResponse> getClaimsByMember(@PathVariable("memberId") Long memberId) {
        return claimService.getClaimsByMember(memberId).stream().map(ClaimResponse::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/disaster/{disasterId}")
    public List<ClaimResponse> getClaimsByDisaster(@PathVariable("disasterId") Long disasterId) {
        return claimService.getClaimsByDisaster(disasterId).stream().map(ClaimResponse::from).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}")
    public List<ClaimResponse> getClaimsByRegion(@PathVariable("regionId") Long regionId) {
        return claimService.getClaimsByRegion(regionId).stream().map(ClaimResponse::from).toList();
    }

    /** Admin releases the payout for an approved claim. */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pay/{claimId}")
    public ClaimResponse payClaim(@PathVariable("claimId") Long claimId) {
        Claim claim = claimService.payClaim(claimId);
        return ClaimResponse.from(claim);
    }
}
