package org.hartford.mutualinsurance.dto;

import org.hartford.mutualinsurance.entity.Claim;
import org.hartford.mutualinsurance.entity.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Safe response DTO for claims — exposes readable context without circular refs.
 * Used by claim officers (who need member + disaster info) and members (who need status).
 */
public class ClaimResponse {

    private Long id;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private ClaimStatus status;
    private LocalDateTime claimDate;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String documentUrl;
    private String description;

    // Assigned Officer
    private Long assignedOfficerId;
    private String assignedOfficerName;

    // Member context (for claim officer review)
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String regionName;

    // Disaster context (for claim officer review)
    private Long disasterId;
    private String disasterType;
    private String severityLevel;

    /** Constructs a ClaimResponse from a Claim entity. */
    public static ClaimResponse from(Claim claim) {
        ClaimResponse r = new ClaimResponse();
        r.id = claim.getId();
        r.requestedAmount = claim.getRequestedAmount();
        r.approvedAmount = claim.getApprovedAmount();
        r.status = claim.getStatus();
        r.claimDate = claim.getClaimDate();
        r.reviewedBy = claim.getReviewedBy();
        r.reviewedAt = claim.getReviewedAt();
        r.documentUrl = claim.getDocumentUrl();
        r.description = claim.getDescription();

        if (claim.getAssignedOfficer() != null) {
            r.assignedOfficerId = claim.getAssignedOfficer().getId();
            r.assignedOfficerName = claim.getAssignedOfficer().getFullName();
        }

        if (claim.getMember() != null) {
            r.memberId = claim.getMember().getId();
            r.memberName = claim.getMember().getFullName();
            r.memberEmail = claim.getMember().getEmail();
            if (claim.getMember().getRegion() != null) {
                r.regionName = claim.getMember().getRegion().getName();
            }
        }

        if (claim.getDisasterEvent() != null) {
            r.disasterId = claim.getDisasterEvent().getId();
            r.disasterType = claim.getDisasterEvent().getDisasterType();
            r.severityLevel = claim.getDisasterEvent().getSeverityLevel();
        }

        return r;
    }

    // ── Getters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public ClaimStatus getStatus() { return status; }
    public LocalDateTime getClaimDate() { return claimDate; }
    public String getReviewedBy() { return reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public Long getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getMemberEmail() { return memberEmail; }
    public String getRegionName() { return regionName; }
    public Long getDisasterId() { return disasterId; }
    public String getDisasterType() { return disasterType; }
    public String getSeverityLevel() { return severityLevel; }
    public String getDocumentUrl() { return documentUrl; }
    public String getDescription() { return description; }
    public Long getAssignedOfficerId() { return assignedOfficerId; }
    public String getAssignedOfficerName() { return assignedOfficerName; }
}
