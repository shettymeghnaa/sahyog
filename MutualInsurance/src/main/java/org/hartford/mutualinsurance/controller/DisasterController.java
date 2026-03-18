package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.entity.DisasterEvent;
import org.hartford.mutualinsurance.service.DisasterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disasters")
public class DisasterController {
    private final DisasterService disasterService;

    public DisasterController(DisasterService disasterService) {
        this.disasterService = disasterService;
    }

    // -------------------------------------------------------
    // ADMIN ENDPOINTS
    // -------------------------------------------------------

    @PreAuthorize("hasRole('GOVERNMENT')")
    @PostMapping("/region/{regionId}")
    public DisasterEvent declareDisaster(@PathVariable("regionId") Long regionId,
                                         @RequestBody DisasterEvent disasterEvent) {
        return disasterService.declareDisaster(regionId, disasterEvent);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GOVERNMENT')")
    @GetMapping
    public List<DisasterEvent> getAllDisasters() {
        return disasterService.getAllDisasters();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GOVERNMENT')")
    @GetMapping("/region/{regionId}")
    public List<DisasterEvent> getDisastersByRegion(@PathVariable("regionId") Long regionId) {
        return disasterService.getDisastersByRegion(regionId);
    }

    @PreAuthorize("hasRole('GOVERNMENT')")
    @PostMapping("/close/{disasterId}")
    public DisasterEvent closeDisaster(@PathVariable("disasterId") Long disasterId) {
        return disasterService.closeDisaster(disasterId);
    }

    // -------------------------------------------------------
    // MEMBER / CLAIM_OFFICER ENDPOINTS
    // -------------------------------------------------------

    /**
     * Returns all ACTIVE disasters — used by members when submitting claims
     * and by claim officers for context. Excludes CLOSED disasters.
     */
    @PreAuthorize("hasAnyRole('MEMBER', 'CLAIM_OFFICER')")
    @GetMapping("/active")
    public List<DisasterEvent> getActiveDisasters() {
        return disasterService.getActiveDisasters();
    }

    /**
     * Returns ACTIVE disasters for a specific region.
     * Members use this to see only disasters relevant to their own region.
     */
    @PreAuthorize("hasAnyRole('MEMBER', 'CLAIM_OFFICER')")
    @GetMapping("/active/region/{regionId}")
    public List<DisasterEvent> getActiveDisastersByRegion(@PathVariable("regionId") Long regionId) {
        return disasterService.getActiveDisastersByRegion(regionId);
    }
}
