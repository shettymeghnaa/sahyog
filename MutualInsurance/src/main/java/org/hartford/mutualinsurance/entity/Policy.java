package org.hartford.mutualinsurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal monthlyContribution;

    @Column(nullable = false)
    private Integer waitingPeriodDays;

    @Column(nullable = false)
    private BigDecimal maxPayoutPerClaim;

    @Column(nullable = false)
    private BigDecimal annualPayoutCap;

    @Column(nullable = true)
    private Integer minContributionsRequired;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "region_id", nullable = false, unique = true)
    @JsonIgnore
    private Region region;

    public Policy() {
        this.createdAt = LocalDateTime.now();
    }

    public Policy(Long id, BigDecimal monthlyContribution, Integer waitingPeriodDays, BigDecimal maxPayoutPerClaim, BigDecimal annualPayoutCap, Integer minContributionsRequired, LocalDateTime createdAt, Region region) {
        this.id = id;
        this.monthlyContribution = monthlyContribution;
        this.waitingPeriodDays = waitingPeriodDays;
        this.maxPayoutPerClaim = maxPayoutPerClaim;
        this.annualPayoutCap = annualPayoutCap;
        this.minContributionsRequired = minContributionsRequired;
        this.createdAt = createdAt;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }

    public Integer getWaitingPeriodDays() {
        return waitingPeriodDays;
    }

    public void setWaitingPeriodDays(Integer waitingPeriodDays) {
        this.waitingPeriodDays = waitingPeriodDays;
    }

    public BigDecimal getMaxPayoutPerClaim() {
        return maxPayoutPerClaim;
    }

    public void setMaxPayoutPerClaim(BigDecimal maxPayoutPerClaim) {
        this.maxPayoutPerClaim = maxPayoutPerClaim;
    }

    public BigDecimal getAnnualPayoutCap() {
        return annualPayoutCap;
    }

    public void setAnnualPayoutCap(BigDecimal annualPayoutCap) {
        this.annualPayoutCap = annualPayoutCap;
    }

    public Integer getMinContributionsRequired() {
        return minContributionsRequired;
    }

    public void setMinContributionsRequired(Integer minContributionsRequired) {
        this.minContributionsRequired = minContributionsRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", monthlyContribution=" + monthlyContribution +
                ", waitingPeriodDays=" + waitingPeriodDays +
                ", maxPayoutPerClaim=" + maxPayoutPerClaim +
                ", annualPayoutCap=" + annualPayoutCap +
                ", createdAt=" + createdAt +
                ", region=" + region +
                '}';
    }
}
