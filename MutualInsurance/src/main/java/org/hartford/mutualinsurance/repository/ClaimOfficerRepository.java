package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.ClaimOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ClaimOfficerRepository extends JpaRepository<ClaimOfficer, Long> {
    boolean existsByEmail(String email);
    java.util.Optional<org.hartford.mutualinsurance.entity.ClaimOfficer> findByEmail(String email);
    java.util.List<ClaimOfficer> findByRegionIdAndStatus(Long regionId, String status);

    @Query("SELECT co FROM ClaimOfficer co " +
           "LEFT JOIN Claim c ON c.assignedOfficer = co AND (c.status = org.hartford.mutualinsurance.entity.ClaimStatus.SUBMITTED OR c.status = org.hartford.mutualinsurance.entity.ClaimStatus.UNDER_REVIEW) " +
           "WHERE co.region.id = :regionId AND co.status = 'ACTIVE' " +
           "GROUP BY co " +
           "ORDER BY COUNT(c) ASC")
    List<ClaimOfficer> findOfficersByWorkload(@Param("regionId") Long regionId);
}
