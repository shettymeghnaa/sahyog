package org.hartford.mutualinsurance.dto;

import java.math.BigDecimal;

public class PoolFinancialSummary {
    private BigDecimal totalBalance;
    private BigDecimal reserveAmount;
    private BigDecimal availableBalance;

    private Long totalMembers;
    private BigDecimal totalContributions;
    private BigDecimal totalClaimsPaid;
    private Long pendingClaims;

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(BigDecimal reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public BigDecimal getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(BigDecimal totalContributions) {
        this.totalContributions = totalContributions;
    }

    public BigDecimal getTotalClaimsPaid() {
        return totalClaimsPaid;
    }

    public void setTotalClaimsPaid(BigDecimal totalClaimsPaid) {
        this.totalClaimsPaid = totalClaimsPaid;
    }

    public Long getPendingClaims() {
        return pendingClaims;
    }

    public void setPendingClaims(Long pendingClaims) {
        this.pendingClaims = pendingClaims;
    }
}
