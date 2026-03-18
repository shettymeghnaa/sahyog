package org.hartford.mutualinsurance.dto;

import org.hartford.mutualinsurance.entity.Policy;
import org.hartford.mutualinsurance.entity.PoolFund;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PolicyDTO {
    private Long id;
    private BigDecimal monthlyContribution;
    private Integer waitingPeriodDays;
    private BigDecimal maxPayoutPerClaim;
    private BigDecimal annualPayoutCap;
    private Integer minContributionsRequired;
    private BigDecimal reservePercentage;
    private LocalDateTime createdAt;

    public static PolicyDTO from(Policy policy, PoolFund poolFund) {
        PolicyDTO dto = new PolicyDTO();
        if (policy != null) {
            dto.setId(policy.getId());
            dto.setMonthlyContribution(policy.getMonthlyContribution());
            dto.setWaitingPeriodDays(policy.getWaitingPeriodDays());
            dto.setMaxPayoutPerClaim(policy.getMaxPayoutPerClaim());
            dto.setAnnualPayoutCap(policy.getAnnualPayoutCap());
            dto.setMinContributionsRequired(policy.getMinContributionsRequired());
            dto.setCreatedAt(policy.getCreatedAt());
        }
        if (poolFund != null) {
            dto.setReservePercentage(poolFund.getReservePercentage());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
    public Integer getWaitingPeriodDays() { return waitingPeriodDays; }
    public void setWaitingPeriodDays(Integer waitingPeriodDays) { this.waitingPeriodDays = waitingPeriodDays; }
    public BigDecimal getMaxPayoutPerClaim() { return maxPayoutPerClaim; }
    public void setMaxPayoutPerClaim(BigDecimal maxPayoutPerClaim) { this.maxPayoutPerClaim = maxPayoutPerClaim; }
    public BigDecimal getAnnualPayoutCap() { return annualPayoutCap; }
    public void setAnnualPayoutCap(BigDecimal annualPayoutCap) { this.annualPayoutCap = annualPayoutCap; }
    public Integer getMinContributionsRequired() { return minContributionsRequired; }
    public void setMinContributionsRequired(Integer minContributionsRequired) { this.minContributionsRequired = minContributionsRequired; }
    public BigDecimal getReservePercentage() { return reservePercentage; }
    public void setReservePercentage(BigDecimal reservePercentage) { this.reservePercentage = reservePercentage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
