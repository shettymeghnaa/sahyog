package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.entity.Claim;
import org.hartford.mutualinsurance.entity.Contribution;
import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.service.ClaimService;
import org.hartford.mutualinsurance.service.ContributionService;
import org.hartford.mutualinsurance.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final ClaimService claimService;
    private final ContributionService contributionService;

    public MemberController(MemberService memberService,
            ClaimService claimService,
            ContributionService contributionService) {
        this.memberService = memberService;
        this.claimService = claimService;
        this.contributionService = contributionService;
    }

    @PostMapping("/region/{regionId}")
    public Member registerMember(@PathVariable("regionId") Long regionId,
            @RequestBody Member member) {

        return memberService.registerMember(regionId, member);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/me")
    public Member getMyProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            return memberService.getMemberByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable("id") Long id) {
        return memberService.getMemberById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/region/{regionId}")
    public List<Member> getMembersByRegion(@PathVariable("regionId") Long regionId) {
        return memberService.getMembersByRegion(regionId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/suspend/{memberId}")
    public Member suspendMember(@PathVariable("memberId") Long memberId) {
        return memberService.suspendMember(memberId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/activate/{memberId}")
    public Member activateMember(@PathVariable("memberId") Long memberId) {
        return memberService.activateMember(memberId);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/{memberId}/claims")
    public List<Claim> getMemberClaims(@PathVariable("memberId") Long memberId) {
        return claimService.getClaimsByMember(memberId);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/{memberId}/contributions")
    public List<Contribution> getMemberContributions(@PathVariable("memberId") Long memberId) {
        return contributionService.getMemberContributions(memberId);
    }
}
