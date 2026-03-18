package org.hartford.mutualinsurance.controller;
import org.hartford.mutualinsurance.entity.Contribution;
import org.hartford.mutualinsurance.service.ContributionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.time.YearMonth;
@RestController
@RequestMapping("/api/contributions")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/pay")
    public Contribution payContribution(
            Authentication authentication,
            @RequestParam("month") String month){

        String email = authentication.getName();

        return contributionService.payContributionByEmail(
                email,
                LocalDate.parse(month).withDayOfMonth(1)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}")
    public List<Contribution> getContributionsByMember(
            @PathVariable("memberId") Long memberId){

        return contributionService.getMemberContributions(memberId);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my")
    public List<Contribution> getMyContributions(Authentication authentication){

        String email = authentication.getName();

        return contributionService.getMemberContributionsByEmail(email);
    }
}