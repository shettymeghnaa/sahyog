package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.service.RegionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Region createRegion(@RequestBody Region region) {
        return regionService.createRegion(region);
    }

    @GetMapping
    public List<Region> getAllRegions() {
        return regionService.getAllRegions();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Region getRegionById(@PathVariable("id") Long id) {
        return regionService.getRegionById(id);
    }

    @PreAuthorize("hasRole('GOVERNMENT')")
    @PutMapping("/{id}/risk")
    public Region updateRiskLevel(@PathVariable("id") Long id, @RequestParam("level") String level) {
        return regionService.updateRiskLevel(id, level);
    }
}
