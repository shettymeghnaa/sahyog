package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.*;
import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClaimRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClaimRepository claimRepository;

    @Test
    void shouldSumApprovedClaimsByRegionId() {
        Region region = new Region();
        region.setName("North");
        region.setState("Punjab");
        region.setCountry("India");
        region.setStatus("ACTIVE");
        region = entityManager.persistAndFlush(region);

        AppUser memberUser = new AppUser();
        memberUser.setUsername("member@test.com");
        memberUser.setPassword("pass");
        memberUser.setRole(Role.ROLE_MEMBER);
        memberUser = entityManager.persistAndFlush(memberUser);

        Member member = new Member();
        member.setUser(memberUser);
        member.setFullName("Member Test");
        member.setEmail("member@test.com");
        member.setRegion(region);
        member.setStatus(MemberStatus.ACTIVE);
        member = entityManager.persistAndFlush(member);

        DisasterEvent disaster = new DisasterEvent();
        disaster.setName("Flood 2024");
        disaster.setDisasterType("Flood");
        disaster.setRegion(region);
        disaster.setStartDate(LocalDate.now());
        disaster.setSeverityLevel("HIGH");
        disaster.setStatus("ACTIVE");
        disaster = entityManager.persistAndFlush(disaster);

        Claim claim1 = new Claim();
        claim1.setMember(member);
        claim1.setDisasterEvent(disaster);
        claim1.setRequestedAmount(new BigDecimal("2000.00"));
        claim1.setApprovedAmount(new BigDecimal("1000.00"));
        claim1.setStatus(ClaimStatus.APPROVED);
        entityManager.persist(claim1);

        // For claim2, we need a different disaster or member to avoid unique constraint violation if applicable
        // The unique constraint is (member_id, disaster_id). So we need another disaster.
        DisasterEvent disaster2 = new DisasterEvent();
        disaster2.setName("Flood 2025");
        disaster2.setDisasterType("Flood");
        disaster2.setRegion(region);
        disaster2.setStartDate(LocalDate.now().plusYears(1));
        disaster2.setSeverityLevel("HIGH");
        disaster2.setStatus("ACTIVE");
        disaster2 = entityManager.persistAndFlush(disaster2);

        Claim claim2 = new Claim();
        claim2.setMember(member);
        claim2.setDisasterEvent(disaster2);
        claim2.setRequestedAmount(new BigDecimal("1000.00"));
        claim2.setApprovedAmount(new BigDecimal("500.00"));
        claim2.setStatus(ClaimStatus.APPROVED);
        entityManager.persist(claim2);

        entityManager.flush();

        BigDecimal sum = claimRepository.sumApprovedByRegionId(region.getId());
        assertThat(sum).isEqualByComparingTo("1500.00");
    }

    @Test
    void shouldCountPendingClaimsByRegionId() {
        Region region = new Region();
        region.setName("South");
        region.setState("Kerala");
        region.setCountry("India");
        region.setStatus("ACTIVE");
        region = entityManager.persistAndFlush(region);

        AppUser memberUser = new AppUser();
        memberUser.setUsername("m2@test.com");
        memberUser.setPassword("pass");
        memberUser.setRole(Role.ROLE_MEMBER);
        memberUser = entityManager.persistAndFlush(memberUser);

        Member member = new Member();
        member.setUser(memberUser);
        member.setFullName("Member 2 Test");
        member.setEmail("m2@test.com");
        member.setRegion(region);
        member.setStatus(MemberStatus.ACTIVE);
        member = entityManager.persistAndFlush(member);

        DisasterEvent disaster = new DisasterEvent();
        disaster.setName("Cyclone 2024");
        disaster.setDisasterType("Cyclone");
        disaster.setRegion(region);
        disaster.setStartDate(LocalDate.now());
        disaster.setSeverityLevel("CRITICAL");
        disaster.setStatus("ACTIVE");
        disaster = entityManager.persistAndFlush(disaster);

        Claim claim = new Claim();
        claim.setMember(member);
        claim.setDisasterEvent(disaster);
        claim.setRequestedAmount(new BigDecimal("5000.00"));
        claim.setStatus(ClaimStatus.SUBMITTED);
        entityManager.persistAndFlush(claim);

        Long count = claimRepository.countPendingByRegionId(region.getId());
        assertThat(count).isEqualTo(1L);
    }
}
