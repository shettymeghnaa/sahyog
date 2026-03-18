package org.hartford.mutualinsurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_verifications")
public class ClaimVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonIgnore
    private Claim claim;

    @ManyToOne
    @JoinColumn(name = "officer_id", nullable = false)
    @JsonIgnore
    private ClaimOfficer officer;

    @Column(length = 2000)
    private String verificationNotes;

    @Column(nullable = false)
    private String decision; // APPROVE or REJECT

    private LocalDateTime verifiedAt;

    public ClaimVerification() {
        this.verifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }

    public ClaimOfficer getOfficer() {
        return officer;
    }

    public void setOfficer(ClaimOfficer officer) {
        this.officer = officer;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
