package org.hartford.mutualinsurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hartford.mutualinsurance.security.AppUser;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean acceptedTerms;

    private String phoneNumber;

    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false)
    private BigDecimal totalContribution = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    @JsonIgnoreProperties("members")
    private Region region;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Member() {
        this.joinDate = LocalDate.now();
        this.status = MemberStatus.ACTIVE;
    }

    public Member(Long id, String fullName, String email, LocalDate joinDate, MemberStatus status, BigDecimal totalContribution, Region region) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.joinDate = joinDate;
        this.status = status;
        this.totalContribution = totalContribution;
        this.region = region;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", status=" + status +
                ", totalContribution=" + totalContribution +
                ", region=" + region +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(BigDecimal totalContribution) {
        this.totalContribution = totalContribution;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
