package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Contribution;
import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.entity.MemberStatus;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.ContributionRepository;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.repository.PolicyRepository;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;
    private final PolicyRepository policyRepository;
    private final PoolFundService poolFundService;

    public ContributionServiceImpl(
            ContributionRepository contributionRepository,
            MemberRepository memberRepository,
            PolicyRepository policyRepository,
            PoolFundService poolFundService) {

        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
        this.policyRepository = policyRepository;
        this.poolFundService = poolFundService;
    }

    @Override
    public Contribution payContributionByEmail(String email, LocalDate month) {

        // 1️⃣ Get member using email from JWT
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if(member.getStatus() != MemberStatus.ACTIVE){
            throw new InvalidOperationException("Member is not active");
        }

        // 2️⃣ Normalize month
        LocalDate normalizedMonth = month.withDayOfMonth(1);
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        if (!normalizedMonth.equals(currentMonth)) {
            throw new InvalidOperationException("Contributions can only be paid for the current month");
        }

        // 3️⃣ Prevent duplicate payment
        if(contributionRepository
                .findByMemberIdAndContributionMonth(member.getId(), normalizedMonth)
                .isPresent()){

            throw new InvalidOperationException("Contribution already paid for this month");
        }

        // 4️⃣ Fetch region policy
        var policy = policyRepository.findByRegionId(member.getRegion().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

        BigDecimal baseContribution = policy.getMonthlyContribution();
        BigDecimal contributionAmount = baseContribution;
        
        String riskLevel = member.getRegion().getRiskLevel();
        if ("MEDIUM".equalsIgnoreCase(riskLevel)) {
            contributionAmount = baseContribution.multiply(new BigDecimal("1.25"));
        } else if ("HIGH".equalsIgnoreCase(riskLevel)) {
            contributionAmount = baseContribution.multiply(new BigDecimal("1.50"));
        }

        // 5️⃣ Calculate admin fee
        BigDecimal adminFee = contributionAmount
                .multiply(member.getRegion().getPoolFund().getAdminFeePercentage())
                .divide(new BigDecimal("100"));

        BigDecimal netAmount = contributionAmount.subtract(adminFee);

        // 6️⃣ Update pool fund
        poolFundService.updateBalance(member.getRegion().getPoolFund(), netAmount);

        // 7️⃣ Update member contribution total
        member.setTotalContribution(
                member.getTotalContribution().add(contributionAmount)
        );

        memberRepository.save(member);

        // 8️⃣ Save contribution record
        Contribution contribution = new Contribution();
        contribution.setMember(member);
        contribution.setAmount(contributionAmount);
        contribution.setContributionMonth(normalizedMonth);

        return contributionRepository.save(contribution);
    }

    @Override
    public List<Contribution> getMemberContributions(Long memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return contributionRepository.findByMemberId(memberId);
    }

    @Override
    public List<Contribution> getMemberContributionsByEmail(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return contributionRepository.findByMemberId(member.getId());
    }
}