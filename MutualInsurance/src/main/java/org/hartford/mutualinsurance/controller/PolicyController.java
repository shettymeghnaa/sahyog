package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.dto.PolicyDTO;
import org.hartford.mutualinsurance.service.PolicyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/region/{regionId}")
    public PolicyDTO createPolicy(@PathVariable("regionId") Long regionId,
                                  @RequestBody PolicyDTO policy) {
        return policyService.createPolicy(regionId, policy);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @GetMapping("/region/{regionId}")
    public PolicyDTO getPolicyByRegion(@PathVariable("regionId") Long regionId) {
        return policyService.getPolicyByRegionId(regionId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{policyId}")
    public PolicyDTO updatePolicy(@PathVariable("policyId") Long policyId,
                                  @RequestBody PolicyDTO updatedPolicy) {
        return policyService.updatePolicy(policyId, updatedPolicy);
    }
}
