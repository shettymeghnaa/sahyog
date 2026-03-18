package org.hartford.mutualinsurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pool_funds")
public class PoolFund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal totalBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal reservePercentage;

    @Column(nullable = false)
    private BigDecimal adminFeePercentage;

    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "region_id", nullable = false, unique = true)
    @JsonIgnore
    private Region region;

    public PoolFund() {
        this.lastUpdated = LocalDateTime.now();
    }

    public PoolFund(Long id, BigDecimal totalBalance, BigDecimal reservePercentage, BigDecimal adminFeePercentage, LocalDateTime lastUpdated, Region region) {
        this.id = id;
        this.totalBalance = totalBalance;
        this.reservePercentage = reservePercentage;
        this.adminFeePercentage = adminFeePercentage;
        this.lastUpdated = lastUpdated;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getReservePercentage() {
        return reservePercentage;
    }

    public void setReservePercentage(BigDecimal reservePercentage) {
        this.reservePercentage = reservePercentage;
    }

    public BigDecimal getAdminFeePercentage() {
        return adminFeePercentage;
    }

    public void setAdminFeePercentage(BigDecimal adminFeePercentage) {
        this.adminFeePercentage = adminFeePercentage;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "PoolFund{" +
                "id=" + id +
                ", totalBalance=" + totalBalance +
                ", reservePercentage=" + reservePercentage +
                ", adminFeePercentage=" + adminFeePercentage +
                ", lastUpdated=" + lastUpdated +
                ", region=" + region +
                '}';
    }
}
