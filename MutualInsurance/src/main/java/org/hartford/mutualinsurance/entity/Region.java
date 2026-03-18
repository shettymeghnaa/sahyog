package org.hartford.mutualinsurance.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "regions")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String state;

    private String country;

    private String riskLevel;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "region", cascade = CascadeType.ALL)
    private Policy policy;

    @OneToOne(mappedBy = "region", cascade = CascadeType.ALL)
    private PoolFund poolFund;

    @OneToMany(mappedBy = "region")
    private List<Member> members;

    public Region(Long id, String name, String state, String country, String riskLevel, String status, LocalDateTime createdAt, Policy policy, PoolFund poolFund, List<Member> members) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.country = country;
        this.riskLevel = riskLevel;
        this.status = status;
        this.createdAt = createdAt;
        this.policy = policy;
        this.poolFund = poolFund;
        this.members = members;
    }

    public Region(String name, String state, String country, String riskLevel, String status, LocalDateTime createdAt, Policy policy, PoolFund poolFund, List<Member> members) {
        this.name = name;
        this.state = state;
        this.country = country;
        this.riskLevel = riskLevel;
        this.status = status;
        this.createdAt = createdAt;
        this.policy = policy;
        this.poolFund = poolFund;
        this.members = members;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public PoolFund getPoolFund() {
        return poolFund;
    }

    public void setPoolFund(PoolFund poolFund) {
        this.poolFund = poolFund;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", policy=" + policy +
                ", poolFund=" + poolFund +
                ", members=" + members +
                '}';
    }

    public Region() {
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }
}
