package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.dto.PoolFinancialSummary;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.service.PoolFundService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
public class PoolFundController {
    private final PoolFundService poolFundService;

    public PoolFundController(PoolFundService poolFundService) {
        this.poolFundService = poolFundService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}")
    public PoolFund getPoolByRegion(@PathVariable("regionId") Long regionId) {
        return poolFundService.getPoolByRegionId(regionId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}/summary")
    public PoolFinancialSummary getFinancialSummary(@PathVariable("regionId") Long regionId) {
        return poolFundService.getFinancialSummary(regionId);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary/global")
    public PoolFinancialSummary getGlobalFinancialSummary() {
        return poolFundService.getGlobalFinancialSummary();
    }
}
