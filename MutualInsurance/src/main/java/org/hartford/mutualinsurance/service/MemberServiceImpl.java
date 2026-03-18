package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.entity.MemberStatus;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.repository.PolicyRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.Role;
import org.hartford.mutualinsurance.security.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final PolicyRepository policyRepository;

    public MemberServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MemberRepository memberRepository,
                             RegionRepository regionRepository,
                             PolicyRepository policyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.regionRepository = regionRepository;
        this.policyRepository = policyRepository;
    }
    @Override
    public Member registerMember(Long regionId, Member member) {

        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));

        if (!member.isAcceptedTerms()) {
            throw new InvalidOperationException("You must accept the Terms and Conditions to register");
        }

        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new InvalidOperationException("Email already registered");
        }

        // CREATE USER
        AppUser user = new AppUser();
        user.setUsername(member.getEmail());
        user.setPassword(passwordEncoder.encode(member.getPassword()));
        user.setRole(Role.ROLE_MEMBER);

        userRepository.save(user);

        // MEMBER SETUP
        member.setUser(user);
        member.setRegion(region);
        member.setStatus(MemberStatus.PENDING);
        member.setJoinDate(LocalDate.now());
        member.setTotalContribution(BigDecimal.ZERO);

        return memberRepository.save(member);
    }

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }

    @Override
    public List<Member> getMembersByRegion(Long regionId) {
        return memberRepository.findByRegionId(regionId);
    }

    @Override
    public Member suspendMember(Long memberId) {
        Member member = getMemberById(memberId);
        member.setStatus(MemberStatus.SUSPENDED);
        return memberRepository.save(member);
    }

    @Override
    public Member activateMember(Long memberId) {
        Member member = getMemberById(memberId);
        member.setStatus(MemberStatus.ACTIVE);
        return memberRepository.save(member);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();    }

    @Override
    public Member getMemberByEmail(String email) {
        AppUser user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return memberRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }

    public Member getMemberByUsername(String username) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return memberRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }
}
