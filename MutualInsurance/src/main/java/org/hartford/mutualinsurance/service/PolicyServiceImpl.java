package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.PolicyDTO;
import org.hartford.mutualinsurance.entity.Policy;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.PolicyRepository;
import org.hartford.mutualinsurance.repository.PoolFundRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PolicyServiceImpl implements PolicyService {
    private final PolicyRepository policyRepository;
    private final RegionRepository regionRepository;
    private final PoolFundRepository poolFundRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository,
                             RegionRepository regionRepository,
                             PoolFundRepository poolFundRepository) {
        this.policyRepository = policyRepository;
        this.regionRepository = regionRepository;
        this.poolFundRepository = poolFundRepository;
    }

    @Override
    public PolicyDTO createPolicy(Long regionId, PolicyDTO dto) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));

        if (policyRepository.findByRegionId(regionId).isPresent()) {
            Policy existing = policyRepository.findByRegionId(regionId).get();
            return updatePolicy(existing.getId(), dto);
        }

        Policy policy = new Policy();
        policy.setRegion(region);
        policy.setMonthlyContribution(dto.getMonthlyContribution());
        policy.setWaitingPeriodDays(dto.getWaitingPeriodDays());
        policy.setMaxPayoutPerClaim(dto.getMaxPayoutPerClaim());
        policy.setMinContributionsRequired(dto.getMinContributionsRequired());
        
        if (dto.getAnnualPayoutCap() == null && dto.getMaxPayoutPerClaim() != null) {
            policy.setAnnualPayoutCap(dto.getMaxPayoutPerClaim().multiply(new BigDecimal("3")));
        } else {
            policy.setAnnualPayoutCap(dto.getAnnualPayoutCap());
        }

        Policy savedPolicy = policyRepository.save(policy);

        PoolFund fund = poolFundRepository.findByRegionId(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("PoolFund not found"));
        
        if (dto.getReservePercentage() != null) {
            fund.setReservePercentage(dto.getReservePercentage());
            poolFundRepository.save(fund);
        }

        return PolicyDTO.from(savedPolicy, fund);
    }

    @Override
    public PolicyDTO getPolicyByRegionId(Long regionId) {
        Policy policy = policyRepository.findByRegionId(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found for region"));
        PoolFund fund = poolFundRepository.findByRegionId(regionId).orElse(null);
        return PolicyDTO.from(policy, fund);
    }

    @Override
    public PolicyDTO updatePolicy(Long policyId, PolicyDTO dto) {
        Policy existing = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        existing.setMonthlyContribution(dto.getMonthlyContribution());
        existing.setWaitingPeriodDays(dto.getWaitingPeriodDays());
        existing.setMaxPayoutPerClaim(dto.getMaxPayoutPerClaim());
        
        if (dto.getMinContributionsRequired() != null) {
            existing.setMinContributionsRequired(dto.getMinContributionsRequired());
        }
        
        if (dto.getAnnualPayoutCap() != null) {
            existing.setAnnualPayoutCap(dto.getAnnualPayoutCap());
        }

        Policy savedPolicy = policyRepository.save(existing);

        PoolFund fund = poolFundRepository.findByRegionId(existing.getRegion().getId()).orElse(null);
        if (fund != null && dto.getReservePercentage() != null) {
            fund.setReservePercentage(dto.getReservePercentage());
            poolFundRepository.save(fund);
        }

        return PolicyDTO.from(savedPolicy, fund);
    }
}
