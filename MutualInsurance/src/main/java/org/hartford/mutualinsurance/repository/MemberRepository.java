package org.hartford.mutualinsurance.repository;

import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.security.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByRegionId(Long regionId);
    Long countByRegionId(Long regionId);

    Optional<Member> findByUser(AppUser user);}
