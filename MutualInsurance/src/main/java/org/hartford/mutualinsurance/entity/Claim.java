package org.hartford.mutualinsurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "disaster_id"})
        })
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal requestedAmount;

    private BigDecimal approvedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    private LocalDateTime claimDate;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(length = 1000)
    private String documentUrl;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "disaster_id", nullable = false)
    @JsonIgnore
    private DisasterEvent disasterEvent;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "assigned_officer_id")
    @JsonIgnore
    private ClaimOfficer assignedOfficer;

    public Claim() {
        this.claimDate = LocalDateTime.now();
        this.status = ClaimStatus.SUBMITTED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public LocalDateTime getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDateTime claimDate) {
        this.claimDate = claimDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public DisasterEvent getDisasterEvent() {
        return disasterEvent;
    }

    public void setDisasterEvent(DisasterEvent disasterEvent) {
        this.disasterEvent = disasterEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClaimOfficer getAssignedOfficer() {
        return assignedOfficer;
    }

    public void setAssignedOfficer(ClaimOfficer assignedOfficer) {
        this.assignedOfficer = assignedOfficer;
    }
}
