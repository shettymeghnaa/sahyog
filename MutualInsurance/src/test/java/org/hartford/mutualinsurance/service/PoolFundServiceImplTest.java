package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.PoolFinancialSummary;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PoolFundServiceImplTest {

    @Mock
    private PoolFundRepository poolFundRepository;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ContributionRepository contributionRepository;
    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private PoolFundServiceImpl poolFundService;

    private PoolFund poolFund;
    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setName("North");

        poolFund = new PoolFund();
        poolFund.setId(1L);
        poolFund.setRegion(region);
        poolFund.setTotalBalance(new BigDecimal("10000"));
        poolFund.setReservePercentage(new BigDecimal("30"));
        poolFund.setAdminFeePercentage(new BigDecimal("5"));
    }

    @Test
    void createPoolForRegion_shouldReturnSavedPool() {
        when(poolFundRepository.save(any(PoolFund.class))).thenAnswer(i -> i.getArguments()[0]);

        PoolFund result = poolFundService.createPoolForRegion(region);

        assertNotNull(result);
        assertEquals(region, result.getRegion());
        assertEquals(BigDecimal.ZERO, result.getTotalBalance());
        verify(poolFundRepository, times(1)).save(any(PoolFund.class));
    }

    @Test
    void getPoolByRegionId_whenExists_shouldReturnPool() {
        when(poolFundRepository.findByRegionId(1L)).thenReturn(Optional.of(poolFund));

        PoolFund result = poolFundService.getPoolByRegionId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPoolByRegionId_whenNotExists_shouldThrowException() {
        when(poolFundRepository.findByRegionId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            poolFundService.getPoolByRegionId(1L));
    }

    @Test
    void updateBalance_shouldUpdateAndSave() {
        poolFundService.updateBalance(poolFund, new BigDecimal("500"));

        assertEquals(new BigDecimal("10500"), poolFund.getTotalBalance());
        verify(poolFundRepository, times(1)).save(poolFund);
    }

    @Test
    void getFinancialSummary_whenValid_shouldReturnSummary() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));
        when(poolFundRepository.findByRegionId(1L)).thenReturn(Optional.of(poolFund));
        when(memberRepository.countByRegionId(1L)).thenReturn(100L);
        when(contributionRepository.sumByRegionId(1L)).thenReturn(new BigDecimal("15000"));
        when(claimRepository.sumApprovedByRegionId(1L)).thenReturn(new BigDecimal("5000"));
        when(claimRepository.countPendingByRegionId(1L)).thenReturn(5L);

        PoolFinancialSummary result = poolFundService.getFinancialSummary(1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("10000"), result.getTotalBalance());
        assertEquals(0, new BigDecimal("3000").compareTo(result.getReserveAmount()));
        assertEquals(0, new BigDecimal("7000").compareTo(result.getAvailableBalance()));
        assertEquals(100L, result.getTotalMembers());
        assertEquals(new BigDecimal("15000"), result.getTotalContributions());
        assertEquals(new BigDecimal("5000"), result.getTotalClaimsPaid());
        assertEquals(5L, result.getPendingClaims());
    }

    @Test
    void getGlobalFinancialSummary_shouldReturnGlobalSummary() {
        when(poolFundRepository.findAll()).thenReturn(List.of(poolFund));
        when(memberRepository.count()).thenReturn(100L);
        when(contributionRepository.sumTotalContributions()).thenReturn(new BigDecimal("15000"));
        when(claimRepository.sumTotalApprovedPayouts()).thenReturn(new BigDecimal("5000"));

        PoolFinancialSummary result = poolFundService.getGlobalFinancialSummary();

        assertNotNull(result);
        assertEquals(new BigDecimal("10000"), result.getTotalBalance());
        assertEquals(0, new BigDecimal("3000").compareTo(result.getReserveAmount()));
        assertEquals(100L, result.getTotalMembers());
    }
}
