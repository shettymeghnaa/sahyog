package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.ClaimVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimVerificationRepository extends JpaRepository<ClaimVerification, Long> {
    Optional<ClaimVerification> findByClaimId(Long claimId);
}
