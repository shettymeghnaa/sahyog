package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.Claim;
import org.hartford.mutualinsurance.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByMemberIdAndDisasterEventId(Long memberId, Long disasterId);

    List<Claim> findByMemberId(Long memberId);

    List<Claim> findByStatus(ClaimStatus status);

    List<Claim> findByDisasterEventId(Long disasterId);

    @Query("SELECT COALESCE(SUM(c.approvedAmount),0) FROM Claim c WHERE c.member.region.id = :regionId AND c.status = 'APPROVED'")
    BigDecimal sumApprovedByRegionId(@Param("regionId") Long regionId);

    @Query("SELECT COALESCE(SUM(c.approvedAmount),0) FROM Claim c WHERE c.status = 'APPROVED'")
    BigDecimal sumTotalApprovedPayouts();

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.member.region.id = :regionId AND c.status = 'SUBMITTED'")
    Long countPendingByRegionId(@Param("regionId") Long regionId);

    List<Claim> findByMemberRegionId(Long regionId);
    List<Claim> findByAssignedOfficerEmail(String email);
    List<Claim> findByAssignedOfficerId(Long officerId);
}
