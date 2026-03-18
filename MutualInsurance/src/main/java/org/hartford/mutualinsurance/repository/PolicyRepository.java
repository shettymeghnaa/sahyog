package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByRegionId(Long regionId);
}
