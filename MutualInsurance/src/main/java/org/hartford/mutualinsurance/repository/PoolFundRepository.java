package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.PoolFund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PoolFundRepository extends JpaRepository<PoolFund, Long> {
    Optional<PoolFund> findByRegionId(Long regionId);
}
