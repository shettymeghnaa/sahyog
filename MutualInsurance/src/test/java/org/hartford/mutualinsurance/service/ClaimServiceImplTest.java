package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.ClaimRequest;
import org.hartford.mutualinsurance.entity.*;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private DisasterRepository disasterRepository;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private ContributionRepository contributionRepository;
    @Mock
    private PoolFundService poolFundService;
    @Mock
    private ClaimOfficerRepository claimOfficerRepository;
    @Mock
    private ClaimVerificationRepository verificationRepository;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private Member activeMember;
    private DisasterEvent activeDisaster;
    private Region region;
    private ClaimRequest claimRequest;
    private Policy policy;
    private ClaimOfficer activeOfficer;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setName("North");

        activeMember = new Member();
        activeMember.setId(1L);
        activeMember.setEmail("member@example.com");
        activeMember.setStatus(MemberStatus.ACTIVE);
        activeMember.setRegion(region);
        activeMember.setJoinDate(LocalDate.now().minusDays(100));

        activeDisaster = new DisasterEvent();
        activeDisaster.setId(1L);
        activeDisaster.setStatus("ACTIVE");
        activeDisaster.setRegion(region);

        claimRequest = new ClaimRequest();
        claimRequest.setDisasterId(1L);
        claimRequest.setRequestedAmount(new BigDecimal("1000"));
        claimRequest.setDescription("Test claim");

        policy = new Policy();
        policy.setRegion(region);
        policy.setWaitingPeriodDays(30);
        policy.setMaxPayoutPerClaim(new BigDecimal("5000"));

        activeOfficer = new ClaimOfficer();
        activeOfficer.setId(1L);
        activeOfficer.setStatus("ACTIVE");
        activeOfficer.setRegion(region);
    }

    @Test
    void submitClaim_whenValidRequest_shouldReturnSavedClaim() {
        // Arrange
        String username = "member@example.com";
        when(memberRepository.findByEmail(username)).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(1L)).thenReturn(Optional.of(activeDisaster));
        when(claimRepository.findByMemberIdAndDisasterEventId(1L, 1L)).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(1L)).thenReturn(Optional.of(policy));
        when(contributionRepository.findByMemberIdAndContributionMonth(eq(1L), any())).thenReturn(Optional.of(new Contribution()));
        when(claimOfficerRepository.findOfficersByWorkload(1L)).thenReturn(List.of(activeOfficer));
        
        Claim savedClaim = new Claim();
        savedClaim.setId(123L);
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        // Act
        Claim result = claimService.submitClaim(username, claimRequest);

        // Assert
        assertNotNull(result);
        assertEquals(123L, result.getId());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void submitClaim_whenMemberNotFound_shouldThrowException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            claimService.submitClaim("unknown", claimRequest));
    }

    @Test
    void submitClaim_whenMemberNotActive_shouldThrowException() {
        activeMember.setStatus(MemberStatus.PENDING);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));

        assertThrows(InvalidOperationException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void submitClaim_whenDisasterNotFound_shouldThrowException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void submitClaim_whenRegionMismatch_shouldThrowException() {
        Region otherRegion = new Region();
        otherRegion.setId(2L);
        activeDisaster.setRegion(otherRegion);

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(anyLong())).thenReturn(Optional.of(activeDisaster));

        assertThrows(InvalidOperationException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void submitClaim_whenDuplicateClaim_shouldThrowException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(anyLong())).thenReturn(Optional.of(activeDisaster));
        when(claimRepository.findByMemberIdAndDisasterEventId(anyLong(), anyLong()))
            .thenReturn(Optional.of(new Claim()));

        assertThrows(InvalidOperationException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void submitClaim_whenWaitingPeriodNotMet_shouldThrowException() {
        activeMember.setJoinDate(LocalDate.now().minusDays(10));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(anyLong())).thenReturn(Optional.of(activeDisaster));
        when(claimRepository.findByMemberIdAndDisasterEventId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(anyLong())).thenReturn(Optional.of(policy));

        assertThrows(InvalidOperationException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void submitClaim_whenContributionNotPaid_shouldThrowException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(anyLong())).thenReturn(Optional.of(activeDisaster));
        when(claimRepository.findByMemberIdAndDisasterEventId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(anyLong())).thenReturn(Optional.of(policy));
        when(contributionRepository.findByMemberIdAndContributionMonth(anyLong(), any()))
            .thenReturn(Optional.empty());

        assertThrows(InvalidOperationException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void submitClaim_whenNoOfficersAvailable_shouldThrowException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(activeMember));
        when(disasterRepository.findById(anyLong())).thenReturn(Optional.of(activeDisaster));
        when(claimRepository.findByMemberIdAndDisasterEventId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(policyRepository.findByRegionId(anyLong())).thenReturn(Optional.of(policy));
        when(contributionRepository.findByMemberIdAndContributionMonth(anyLong(), any())).thenReturn(Optional.of(new Contribution()));
        when(claimOfficerRepository.findOfficersByWorkload(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(InvalidOperationException.class, () -> 
            claimService.submitClaim("member@example.com", claimRequest));
    }

    @Test
    void getClaimsByMember_shouldReturnList() {
        when(claimRepository.findByMemberId(1L)).thenReturn(List.of(new Claim()));
        List<Claim> result = claimService.getClaimsByMember(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void getClaimById_whenExists_shouldReturnClaim() {
        Claim claim = new Claim();
        claim.setId(1L);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        
        Claim result = claimService.getClaimById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getClaimById_whenNotExists_shouldThrowException() {
        when(claimRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> claimService.getClaimById(99L));
    }
}
