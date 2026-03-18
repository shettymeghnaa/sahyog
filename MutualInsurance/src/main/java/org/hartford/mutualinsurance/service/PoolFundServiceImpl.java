package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.dto.PoolFinancialSummary;
import org.hartford.mutualinsurance.entity.PoolFund;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PoolFundServiceImpl implements PoolFundService{
    private final PoolFundRepository poolFundRepository;
    private final RegionRepository regionRepository;
    private final MemberRepository memberRepository;
    private final ContributionRepository contributionRepository;
    private final ClaimRepository claimRepository;

    public PoolFundServiceImpl(PoolFundRepository poolFundRepository,
                               RegionRepository regionRepository,
                               MemberRepository memberRepository,
                               ContributionRepository contributionRepository,
                               ClaimRepository claimRepository) {

        this.poolFundRepository = poolFundRepository;
        this.regionRepository = regionRepository;
        this.memberRepository = memberRepository;
        this.contributionRepository = contributionRepository;
        this.claimRepository = claimRepository;
    }

    @Override
    public PoolFund createPoolForRegion(Region region) {
        PoolFund pool = new PoolFund();
        pool.setRegion(region);
        pool.setTotalBalance(BigDecimal.ZERO);
        pool.setReservePercentage(new BigDecimal("30")); // default
        pool.setAdminFeePercentage(new BigDecimal("5")); // default

        return poolFundRepository.save(pool);
    }

    @Override
    public PoolFund getPoolByRegionId(Long regionId) {
        return poolFundRepository.findByRegionId(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
    }

    @Override
    public void updateBalance(PoolFund pool, BigDecimal amount) {
        pool.setTotalBalance(pool.getTotalBalance().add(amount));
        poolFundRepository.save(pool);
    }

    @Override
    public void save(PoolFund pool) {
        poolFundRepository.save(pool);
    }

    @Override
    public PoolFinancialSummary getFinancialSummary(Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));

        PoolFund pool = poolFundRepository.findByRegionId(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        BigDecimal reserveAmount = pool.getTotalBalance()
                .multiply(pool.getReservePercentage())
                .divide(new BigDecimal("100"));

        BigDecimal availableBalance =
                pool.getTotalBalance().subtract(reserveAmount);

        Long totalMembers =
                memberRepository.countByRegionId(regionId);

        BigDecimal totalContributions =
                contributionRepository.sumByRegionId(regionId);

        BigDecimal totalClaimsPaid =
                claimRepository.sumApprovedByRegionId(regionId);

        Long pendingClaims =
                claimRepository.countPendingByRegionId(regionId);

        PoolFinancialSummary summary = new PoolFinancialSummary();
        summary.setTotalBalance(pool.getTotalBalance());
        summary.setReserveAmount(reserveAmount);
        summary.setAvailableBalance(availableBalance);
        summary.setTotalMembers(totalMembers);
        summary.setTotalContributions(totalContributions);
        summary.setTotalClaimsPaid(totalClaimsPaid);
        summary.setPendingClaims(pendingClaims);

        return summary;
    }

    @Override
    public PoolFinancialSummary getGlobalFinancialSummary() {
        List<PoolFund> allPools = poolFundRepository.findAll();
        
        BigDecimal globalTotalBalance = BigDecimal.ZERO;
        BigDecimal globalReserveAmount = BigDecimal.ZERO;

        for (PoolFund pool : allPools) {
            globalTotalBalance = globalTotalBalance.add(pool.getTotalBalance());
            BigDecimal poolReserve = pool.getTotalBalance()
                    .multiply(pool.getReservePercentage())
                    .divide(new BigDecimal("100"));
            globalReserveAmount = globalReserveAmount.add(poolReserve);
        }

        BigDecimal globalAvailableBalance = globalTotalBalance.subtract(globalReserveAmount);
        Long globalTotalMembers = memberRepository.count();
        BigDecimal globalTotalContributions = contributionRepository.sumTotalContributions();
        BigDecimal globalTotalClaimsPaid = claimRepository.sumTotalApprovedPayouts();

        // Optional, we don't necessarily render this in global, but good to have
        Long globalPendingClaims = 0L;

        PoolFinancialSummary summary = new PoolFinancialSummary();
        summary.setTotalBalance(globalTotalBalance);
        summary.setReserveAmount(globalReserveAmount);
        summary.setAvailableBalance(globalAvailableBalance);
        summary.setTotalMembers(globalTotalMembers);
        summary.setTotalContributions(globalTotalContributions);
        summary.setTotalClaimsPaid(globalTotalClaimsPaid);
        summary.setPendingClaims(globalPendingClaims);

        return summary;
    }
}
