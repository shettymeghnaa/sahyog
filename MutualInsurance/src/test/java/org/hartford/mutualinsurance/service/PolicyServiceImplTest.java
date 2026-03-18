package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.PolicyDTO;
import org.hartford.mutualinsurance.entity.Policy;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.PolicyRepository;
import org.hartford.mutualinsurance.repository.PoolFundRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private PoolFundRepository poolFundRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy policy;
    private Region region;
    private PolicyDTO policyDTO;
    private PoolFund poolFund;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setName("North");

        poolFund = new PoolFund();
        poolFund.setId(1L);
        poolFund.setRegion(region);

        policy = new Policy();
        policy.setId(1L);
        policy.setRegion(region);
        policy.setMonthlyContribution(new BigDecimal("100"));
        policy.setWaitingPeriodDays(30);
        policy.setMaxPayoutPerClaim(new BigDecimal("5000"));

        policyDTO = new PolicyDTO();
        policyDTO.setMonthlyContribution(new BigDecimal("100"));
        policyDTO.setWaitingPeriodDays(30);
        policyDTO.setMaxPayoutPerClaim(new BigDecimal("5000"));
        policyDTO.setReservePercentage(new BigDecimal("10"));
    }

    @Test
    void createPolicy_whenNew_shouldReturnSavedPolicyDTO() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.empty());
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
        when(poolFundRepository.findByRegionId(1L)).thenReturn(Optional.of(poolFund));

        PolicyDTO result = policyService.createPolicy(1L, policyDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getMonthlyContribution());
        verify(policyRepository, times(1)).save(any(Policy.class));
        verify(poolFundRepository, times(1)).save(any(PoolFund.class));
    }

    @Test
    void createPolicy_whenExisting_shouldUpdatePolicy() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.findById(anyLong())).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
        when(poolFundRepository.findByRegionId(anyLong())).thenReturn(Optional.of(poolFund));

        PolicyDTO result = policyService.createPolicy(1L, policyDTO);

        assertNotNull(result);
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void getPolicyByRegionId_whenExists_shouldReturnPolicyDTO() {
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.of(policy));
        when(poolFundRepository.findByRegionId(1L)).thenReturn(Optional.of(poolFund));

        PolicyDTO result = policyService.getPolicyByRegionId(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getMonthlyContribution());
    }

    @Test
    void getPolicyByRegionId_whenNotExists_shouldThrowException() {
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            policyService.getPolicyByRegionId(1L));
    }

    @Test
    void updatePolicy_whenValid_shouldReturnUpdatedPolicyDTO() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);
        when(poolFundRepository.findByRegionId(anyLong())).thenReturn(Optional.of(poolFund));

        PolicyDTO result = policyService.updatePolicy(1L, policyDTO);

        assertNotNull(result);
        verify(policyRepository, times(1)).save(any(Policy.class));
        verify(poolFundRepository, times(1)).save(any(PoolFund.class));
    }

    @Test
    void updatePolicy_whenNotFound_shouldThrowException() {
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            policyService.updatePolicy(1L, policyDTO));
    }
}
