package org.hartford.mutualinsurance.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "contributionMonth"})
        })
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate contributionMonth; // use first day of month

    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnoreProperties({"contributions", "claims", "region", "user"})
    private Member member;

    public Contribution() {
        this.paymentDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getContributionMonth() {
        return contributionMonth;
    }

    public void setContributionMonth(LocalDate contributionMonth) {
        this.contributionMonth = contributionMonth;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
