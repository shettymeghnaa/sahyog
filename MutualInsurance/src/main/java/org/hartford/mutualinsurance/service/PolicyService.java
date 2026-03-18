package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.PolicyDTO;

public interface PolicyService {
    PolicyDTO createPolicy(Long regionId, PolicyDTO policy);

    PolicyDTO getPolicyByRegionId(Long regionId);

    PolicyDTO updatePolicy(Long policyId, PolicyDTO updatedPolicy);

}
