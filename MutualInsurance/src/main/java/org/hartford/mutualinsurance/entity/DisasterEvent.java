package org.hartford.mutualinsurance.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disaster_events")
public class DisasterEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Official event name, e.g. "Cyclone Biparjoy 2024"

    @Column(nullable = false)
    private String disasterType; // FLOOD, CYCLONE etc.

    @Column(nullable = false)
    private String severityLevel;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private String status; // ACTIVE / CLOSED

    private LocalDateTime declaredAt;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    @JsonIgnoreProperties({"members", "policy", "poolFund", "disasters"})
    private Region region;

    @OneToMany(mappedBy = "disasterEvent")
    @JsonIgnore
    private List<Claim> claims;

    public DisasterEvent() {
        this.declaredAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(String disasterType) {
        this.disasterType = disasterType;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDeclaredAt() {
        return declaredAt;
    }

    public void setDeclaredAt(LocalDateTime declaredAt) {
        this.declaredAt = declaredAt;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }
}
