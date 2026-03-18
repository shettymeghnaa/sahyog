package org.hartford.mutualinsurance.controller;

import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.JwtUtil;
import org.hartford.mutualinsurance.security.LoginRequest;
import org.hartford.mutualinsurance.security.Role;
import org.hartford.mutualinsurance.security.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.hartford.mutualinsurance.entity.Member;
import org.hartford.mutualinsurance.entity.MemberStatus;
import org.hartford.mutualinsurance.repository.MemberRepository;
import org.hartford.mutualinsurance.exception.InvalidOperationException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    /**
     * Login endpoint. Returns both token AND role so the frontend can route
     * members, admins and claim officers to their respective dashboards.
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        // Check if MEMBER is PENDING
        if (role.equals("ROLE_MEMBER")) {
            AppUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            Member member = memberRepository.findByUser(user).orElseThrow();
            if (member.getStatus() == MemberStatus.PENDING) {
                throw new InvalidOperationException("Account pending admin approval.");
            }
        }

        String token = jwtUtil.generateToken(request.getUsername(), role);

        return Map.of(
                "token", token,
                "role", role
        );
    }

    /**
     * Admin-only endpoint to create a Claim Officer account.
     * Claim officers cannot self-register; only admins can provision them.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/claim-officer")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registerClaimOfficer(@RequestBody LoginRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username already exists");
        }

        AppUser officer = new AppUser();
        officer.setUsername(request.getUsername());
        officer.setPassword(passwordEncoder.encode(request.getPassword()));
        officer.setRole(Role.ROLE_CLAIM_OFFICER);

        userRepository.save(officer);

        return Map.of(
                "message", "Claim officer account created successfully",
                "username", request.getUsername()
        );
    }
}