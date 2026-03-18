package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.*;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.repository.ContributionRepository;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContributionServiceImplTest {

    @Mock
    private ContributionRepository contributionRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private PoolFundService poolFundService;

    @InjectMocks
    private ContributionServiceImpl contributionService;

    private Member member;
    private Region region;
    private Policy policy;
    private PoolFund poolFund;

    @BeforeEach
    void setUp() {
        poolFund = new PoolFund();
        poolFund.setId(1L);
        poolFund.setAdminFeePercentage(new BigDecimal("5"));

        region = new Region();
        region.setId(1L);
        region.setName("North");
        region.setRiskLevel("LOW");
        region.setPoolFund(poolFund);

        member = new Member();
        member.setId(1L);
        member.setEmail("member@example.com");
        member.setStatus(MemberStatus.ACTIVE);
        member.setRegion(region);
        member.setTotalContribution(BigDecimal.ZERO);

        policy = new Policy();
        policy.setId(1L);
        policy.setMonthlyContribution(new BigDecimal("100"));
        policy.setRegion(region);
    }

    @Test
    void payContributionByEmail_whenValid_shouldReturnSavedContribution() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(contributionRepository.findByMemberIdAndContributionMonth(1L, currentMonth)).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.of(policy));
        when(contributionRepository.save(any(Contribution.class))).thenAnswer(i -> i.getArguments()[0]);

        Contribution result = contributionService.payContributionByEmail("member@example.com", currentMonth);

        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getAmount());
        assertEquals(new BigDecimal("100"), member.getTotalContribution());
        verify(poolFundService, times(1)).updateBalance(any(PoolFund.class), any(BigDecimal.class));
        verify(contributionRepository, times(1)).save(any(Contribution.class));
    }

    @Test
    void payContributionByEmail_whenMemberNotActive_shouldThrowException() {
        member.setStatus(MemberStatus.PENDING);
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));

        assertThrows(InvalidOperationException.class, () -> 
            contributionService.payContributionByEmail("member@example.com", LocalDate.now()));
    }

    @Test
    void payContributionByEmail_whenWrongMonth_shouldThrowException() {
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        LocalDate wrongMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);

        assertThrows(InvalidOperationException.class, () -> 
            contributionService.payContributionByEmail("member@example.com", wrongMonth));
    }

    @Test
    void payContributionByEmail_whenAlreadyPaid_shouldThrowException() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(contributionRepository.findByMemberIdAndContributionMonth(1L, currentMonth)).thenReturn(Optional.of(new Contribution()));

        assertThrows(InvalidOperationException.class, () -> 
            contributionService.payContributionByEmail("member@example.com", currentMonth));
    }

    @Test
    void payContributionByEmail_withMediumRisk_shouldApplyRiskMultiplier() {
        region.setRiskLevel("MEDIUM");
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(contributionRepository.findByMemberIdAndContributionMonth(1L, currentMonth)).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.of(policy));
        when(contributionRepository.save(any(Contribution.class))).thenAnswer(i -> i.getArguments()[0]);

        Contribution result = contributionService.payContributionByEmail("member@example.com", currentMonth);

        // 100 * 1.25 = 125
        assertEquals(new BigDecimal("125.00"), result.getAmount());
    }

    @Test
    void payContributionByEmail_withHighRisk_shouldApplyRiskMultiplier() {
        region.setRiskLevel("HIGH");
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(contributionRepository.findByMemberIdAndContributionMonth(1L, currentMonth)).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.of(policy));
        when(contributionRepository.save(any(Contribution.class))).thenAnswer(i -> i.getArguments()[0]);

        Contribution result = contributionService.payContributionByEmail("member@example.com", currentMonth);

        // 100 * 1.50 = 150
        assertEquals(new BigDecimal("150.00"), result.getAmount());
    }

    @Test
    void getMemberContributions_shouldReturnList() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(contributionRepository.findByMemberId(1L)).thenReturn(List.of(new Contribution()));

        List<Contribution> result = contributionService.getMemberContributions(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getMemberContributionsByEmail_shouldReturnList() {
        when(memberRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(contributionRepository.findByMemberId(1L)).thenReturn(List.of(new Contribution()));

        List<Contribution> result = contributionService.getMemberContributionsByEmail("member@example.com");

        assertFalse(result.isEmpty());
    }
}
