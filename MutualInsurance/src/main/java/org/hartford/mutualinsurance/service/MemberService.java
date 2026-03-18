package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Member;

import java.util.List;

public interface MemberService {
    Member registerMember(Long regionId, Member member);

    Member getMemberById(Long id);

    List<Member> getMembersByRegion(Long regionId);

    Member suspendMember(Long memberId);

    Member activateMember(Long memberId);
    List<Member> getAllMembers();

    Member getMemberByEmail(String email);
}
