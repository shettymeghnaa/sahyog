package org.hartford.mutualinsurance.service;

import org.hartford.mutualinsurance.entity.ClaimOfficer;
import org.hartford.mutualinsurance.entity.Region;
import org.hartford.mutualinsurance.exception.InvalidOperationException;
import org.hartford.mutualinsurance.exception.ResourceNotFoundException;
import org.hartford.mutualinsurance.repository.ClaimOfficerRepository;
import org.hartford.mutualinsurance.repository.RegionRepository;
import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.UserRepository;
import org.hartford.mutualinsurance.security.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClaimOfficerServiceImpl implements ClaimOfficerService {

    private final ClaimOfficerRepository claimOfficerRepository;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ClaimOfficerServiceImpl(ClaimOfficerRepository claimOfficerRepository,
                                   RegionRepository regionRepository,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.claimOfficerRepository = claimOfficerRepository;
        this.regionRepository = regionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClaimOfficer createClaimOfficer(Long regionId, ClaimOfficer officer) {
        if (claimOfficerRepository.existsByEmail(officer.getEmail())) {
            throw new InvalidOperationException("A claim officer with this email already exists.");
        }

        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));

        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(officer.getPassword());
        officer.setPassword(encodedPassword);
        officer.setRegion(region);

        // Create corresponding AppUser so the officer can log in
        if (userRepository.findByUsername(officer.getEmail()).isEmpty()) {
            AppUser user = new AppUser();
            user.setUsername(officer.getEmail());
            user.setPassword(encodedPassword);
            user.setRole(Role.ROLE_CLAIM_OFFICER);
            userRepository.save(user);
        }

        return claimOfficerRepository.save(officer);
    }

    @Override
    public List<ClaimOfficer> getAllClaimOfficers() {
        return claimOfficerRepository.findAll();
    }

    @Override
    @Transactional
    public void deactivateClaimOfficer(Long officerId) {
        ClaimOfficer officer = claimOfficerRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim officer not found"));

        officer.setStatus("INACTIVE");
        claimOfficerRepository.save(officer);

        // Delete associated AppUser so they can't log in
        userRepository.findByUsername(officer.getEmail())
                .ifPresent(userRepository::delete);
    }

    @Override
    @Transactional
    public void reassignOfficerRegion(Long officerId, Long newRegionId) {
        ClaimOfficer officer = claimOfficerRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim officer not found"));

        Region region = regionRepository.findById(newRegionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found"));

        officer.setRegion(region);
        claimOfficerRepository.save(officer);
    }
}
