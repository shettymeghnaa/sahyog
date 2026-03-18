package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.Contribution;

import java.time.LocalDate;
import java.util.List;

public interface ContributionService {
    Contribution payContributionByEmail(String email, LocalDate month);

    List<Contribution> getMemberContributions(Long memberId);
    List<Contribution> getMemberContributionsByEmail(String email);
}
