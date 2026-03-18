package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.*;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hartford.mutualinsurance.entity.ClaimStatus.SUBMITTED;

@Service
public class ClaimServiceImpl implements ClaimService {
    private final ClaimRepository claimRepository;
    private final MemberRepository memberRepository;
    private final DisasterRepository disasterRepository;
    private final PolicyRepository policyRepository;
    private final ContributionRepository contributionRepository;
    private final PoolFundService poolFundService;
    private final ClaimOfficerRepository claimOfficerRepository;
    private final ClaimVerificationRepository verificationRepository;

    public ClaimServiceImpl(ClaimRepository claimRepository,
                            MemberRepository memberRepository,
                            DisasterRepository disasterRepository,
                            PolicyRepository policyRepository,
                            ContributionRepository contributionRepository,
                            PoolFundService poolFundService,
                            ClaimOfficerRepository claimOfficerRepository,
                            ClaimVerificationRepository verificationRepository) {

        this.claimRepository = claimRepository;
        this.memberRepository = memberRepository;
        this.disasterRepository = disasterRepository;
        this.policyRepository = policyRepository;
        this.contributionRepository = contributionRepository;
        this.poolFundService = poolFundService;
        this.claimOfficerRepository = claimOfficerRepository;
        this.verificationRepository = verificationRepository;
    }

    /**
     * Submits a claim. Member is resolved from JWT username — never from client body.
     * Automatically assigns an active ClaimOfficer from the member's region.
     */
    @Override
    public Claim submitClaim(String username, org.hartford.mutualinsurance.dto.ClaimRequest request) {

        // 1️⃣ Resolve member from authenticated username (email)
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new InvalidOperationException("Member is not active");
        }

        // 2️⃣ Disaster validation — must exist and be ACTIVE
        DisasterEvent disaster = disasterRepository.findById(request.getDisasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Disaster not found"));

        if (!disaster.getStatus().equals("ACTIVE")) {
            throw new InvalidOperationException("Disaster is not active");
        }

        // 3️⃣ Region match — member must belong to the same region as the disaster
        if (!member.getRegion().getId().equals(disaster.getRegion().getId())) {
            throw new InvalidOperationException("Member not eligible for this disaster region");
        }

        // 4️⃣ Duplicate claim check
        if (claimRepository
                .findByMemberIdAndDisasterEventId(member.getId(), request.getDisasterId())
                .isPresent()) {
            throw new InvalidOperationException("Claim already submitted for this disaster");
        }

        // 5️⃣ Waiting period check
        Policy policy = policyRepository.findByRegionId(member.getRegion().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found for region"));

        long daysSinceJoin = java.time.temporal.ChronoUnit.DAYS
                .between(member.getJoinDate(), LocalDate.now());

        if (daysSinceJoin < policy.getWaitingPeriodDays()) {
            long daysRemaining = policy.getWaitingPeriodDays() - daysSinceJoin;
            throw new InvalidOperationException(
                    "You are in the waiting period. " + daysRemaining + " day(s) remaining before you can submit a claim."
            );
        }

        // 6️⃣ Contribution compliance — must have paid current month
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        if (contributionRepository
                .findByMemberIdAndContributionMonth(member.getId(), currentMonth)
                .isEmpty()) {
            throw new InvalidOperationException(
                    "You must pay your contribution for " +
                    currentMonth.getMonth().name().charAt(0) +
                    currentMonth.getMonth().name().substring(1).toLowerCase() +
                    " " + currentMonth.getYear() +
                    " before submitting a claim."
            );
        }

        // 7️⃣ Auto-Assign Claim Officer (Least Claims Logic)
        List<ClaimOfficer> activeOfficers = claimOfficerRepository.findOfficersByWorkload(member.getRegion().getId());
        if (activeOfficers.isEmpty()) {
            throw new InvalidOperationException("No active claim officers available in your region. Cannot process claim right now.");
        }
        ClaimOfficer assignedOfficer = activeOfficers.get(0);

        // 8️⃣ Create claim
        Claim claim = new Claim();
        claim.setMember(member);
        claim.setDisasterEvent(disaster);
        claim.setRequestedAmount(request.getRequestedAmount());
        claim.setDocumentUrl(request.getDocumentUrl());
        claim.setDescription(request.getDescription());
        claim.setAssignedOfficer(assignedOfficer);
        claim.setStatus(SUBMITTED);

        return claimRepository.save(claim);
    }

    @Override
    public List<Claim> getClaimsByMember(Long memberId) {
        return claimRepository.findByMemberId(memberId);
    }

    @Override
    public List<Claim> getClaimsByDisaster(Long disasterId) {
        return claimRepository.findByDisasterEventId(disasterId);
    }

    @Override
    public List<Claim> getPendingClaims() {
        return claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == SUBMITTED || c.getStatus() == ClaimStatus.UNDER_REVIEW)
                .toList();
    }

    @Override
    public List<Claim> getPendingClaimsByOfficer(String officerEmail) {
        return claimRepository.findByAssignedOfficerEmail(officerEmail).stream()
                .filter(c -> c.getStatus() == SUBMITTED || c.getStatus() == ClaimStatus.UNDER_REVIEW)
                .toList();
    }

    @Override
    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    @Override
    public Claim reviewClaim(Long claimId, String officerUsername) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        if (claim.getStatus() != SUBMITTED) {
            throw new InvalidOperationException("Only SUBMITTED claims can be reviewed");
        }

        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setReviewedBy(officerUsername);
        claim.setReviewedAt(LocalDateTime.now());
        
        return claimRepository.save(claim);
    }

    /**
     * Officer approves the claim after verifying documents.
     * Status moves to APPROVED. Payout is NOT yet released.
     */
    @Override
    public Claim approveClaim(Long claimId, String officerUsername, String notes) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new InvalidOperationException("Claim must be UNDER_REVIEW before approval");
        }

        ClaimOfficer officer = claimOfficerRepository.findByEmail(officerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        // 1. Create Verification Record
        ClaimVerification verification = new ClaimVerification();
        verification.setClaim(claim);
        verification.setOfficer(officer);
        verification.setVerificationNotes(notes);
        verification.setDecision("APPROVE");
        verificationRepository.save(verification);

        // 2. Set Status to APPROVED (Pending Payout)
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setReviewedBy(officerUsername);
        claim.setReviewedAt(LocalDateTime.now());

        return claimRepository.save(claim);
    }

    /**
     * Admin finalizes the claim and releases the actual payout.
     * Status moves to PAID. Fund deduction happens here.
     */
    @Override
    public Claim payClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new InvalidOperationException("Claim must be APPROVED by an officer before payout");
        }

        Member member = claim.getMember();
        Region region = member.getRegion();

        Policy policy = policyRepository.findByRegionId(region.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        PoolFund pool = poolFundService.getPoolByRegionId(region.getId());

        BigDecimal payoutAmount = claim.getRequestedAmount()
                .min(policy.getMaxPayoutPerClaim());

        BigDecimal reserveAmount = pool.getTotalBalance()
                .multiply(pool.getReservePercentage())
                .divide(new BigDecimal("100"));

        BigDecimal availableBalance = pool.getTotalBalance().subtract(reserveAmount);

        if (payoutAmount.compareTo(availableBalance) > 0) {
            throw new InvalidOperationException("Insufficient funds in pool for region " + region.getName());
        }

        // Deduct from pool
        pool.setTotalBalance(pool.getTotalBalance().subtract(payoutAmount));
        poolFundService.save(pool);

        // Finalize Claim
        claim.setApprovedAmount(payoutAmount);
        claim.setStatus(ClaimStatus.PAID);

        return claimRepository.save(claim);
    }

    @Override
    public List<Claim> getClaimsByRegion(Long regionId) {
        return claimRepository.findByMemberRegionId(regionId);
    }

    /**
     * Rejects a claim. Records the officer's username and timestamp.
     */
    @Override
    public Claim rejectClaim(Long claimId, String officerUsername, String notes) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new InvalidOperationException("Claim must be UNDER_REVIEW before rejection");
        }

        ClaimOfficer officer = claimOfficerRepository.findByEmail(officerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        // 1. Create Verification Record
        ClaimVerification verification = new ClaimVerification();
        verification.setClaim(claim);
        verification.setOfficer(officer);
        verification.setVerificationNotes(notes);
        verification.setDecision("REJECT");
        verificationRepository.save(verification);

        // 2. Set Status
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setReviewedBy(officerUsername);
        claim.setReviewedAt(LocalDateTime.now());

        return claimRepository.save(claim);
    }

    @Override
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    @Override
    public List<Claim> getClaimsByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return claimRepository.findByMemberId(member.getId());
    }

    @Override
    public void reassignClaims(Long fromOfficerId, Long toOfficerId) {
        ClaimOfficer toOfficer = claimOfficerRepository.findById(toOfficerId)
                .orElseThrow(() -> new ResourceNotFoundException("Target officer not found"));

        List<Claim> claims = claimRepository.findByAssignedOfficerId(fromOfficerId);
        for (Claim claim : claims) {
            claim.setAssignedOfficer(toOfficer);
        }
        claimRepository.saveAll(claims);
    }
}
