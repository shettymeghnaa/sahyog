package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    Optional<Contribution> findByMemberIdAndContributionMonth(Long memberId, LocalDate month);

    List<Contribution> findByMemberId(Long memberId);

    @Query("SELECT COALESCE(SUM(c.amount),0) FROM Contribution c WHERE c.member.region.id = :regionId")
    BigDecimal sumByRegionId(@Param("regionId") Long regionId);

    @Query("SELECT COALESCE(SUM(c.amount),0) FROM Contribution c")
    BigDecimal sumTotalContributions();
}
