package org.hartford.mutualinsurance.config;

import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.Role;
import org.hartford.mutualinsurance.security.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("member").isEmpty()) {
                AppUser member = new AppUser();
                member.setUsername("member");
                member.setPassword(passwordEncoder.encode("member123"));
                member.setRole(Role.ROLE_MEMBER);
                userRepository.save(member);
            }

            if (userRepository.findByUsername("government@gmail.com").isEmpty()) {
                // H2 natively locks ENUMs. This expands it so we don't crash before the user reaches the console.
                try {
                    jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN role SET DATA TYPE ENUM('ROLE_ADMIN', 'ROLE_CLAIM_OFFICER', 'ROLE_MEMBER', 'ROLE_GOVERNMENT')");
                } catch (Exception e) {
                    System.out.println("Schema update skipped: " + e.getMessage());
                }

                AppUser gov = new AppUser();
                gov.setUsername("government@gmail.com");
                gov.setPassword(passwordEncoder.encode("gov123"));
                gov.setRole(Role.ROLE_GOVERNMENT);
                userRepository.save(gov);
            }

            // Expand ClaimStatus ENUM
            try {
                jdbcTemplate.execute("ALTER TABLE claims ALTER COLUMN status SET DATA TYPE ENUM('PENDING', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'PAID')");
                jdbcTemplate.execute("UPDATE claims SET status = 'SUBMITTED' WHERE status = 'PENDING'");
                jdbcTemplate.execute("UPDATE claims SET status = 'APPROVED' WHERE status = 'PAID'");
            } catch (Exception e) {
                System.out.println("Claim Schema update skipped: " + e.getMessage());
            }

            // Expand MemberStatus ENUM for Registration Approval Workflow
            try {
                jdbcTemplate.execute("ALTER TABLE members ALTER COLUMN status SET DATA TYPE ENUM('PENDING', 'ACTIVE', 'SUSPENDED')");
            } catch (Exception e) {
                System.out.println("Member Schema update skipped: " + e.getMessage());
            }
        };
    }
}
