package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.entity.ClaimOfficer;
import org.hartford.mutualinsurance.service.ClaimOfficerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-officers")
public class ClaimOfficerController {

    private final ClaimOfficerService claimOfficerService;

    public ClaimOfficerController(ClaimOfficerService claimOfficerService) {
        this.claimOfficerService = claimOfficerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/region/{regionId}")
    public ClaimOfficer createClaimOfficer(@PathVariable("regionId") Long regionId,
                                           @RequestBody ClaimOfficer officer) {
        return claimOfficerService.createClaimOfficer(regionId, officer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ClaimOfficer> getAllClaimOfficers() {
        return claimOfficerService.getAllClaimOfficers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public void deactivateClaimOfficer(@PathVariable("id") Long id) {
        claimOfficerService.deactivateClaimOfficer(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reassign-region/{regionId}")
    public void reassignOfficerRegion(@PathVariable("id") Long id,
                                      @PathVariable("regionId") Long regionId) {
        claimOfficerService.reassignOfficerRegion(id, regionId);
    }
}
