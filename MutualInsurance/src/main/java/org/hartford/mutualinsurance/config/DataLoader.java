package org.hartford.mutualinsurance.config;

import org.hartford.mutualinsurance.security.AppUser;
import org.hartford.mutualinsurance.security.Role;
import org.hartford.mutualinsurance.security.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the default admin account on every startup (idempotent — skips if already exists).
 *
 * Default credentials:
 *   username : admin@sahyog.com
 *   password : Admin@123
 *
 * Change these after first login in production!
 */
@Component
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin("admin@sahyog.com", "Admin@123");
    }

    private void seedAdmin(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            return; // already seeded — do nothing
        }

        AppUser admin = new AppUser();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setRole(Role.ROLE_ADMIN);

        userRepository.save(admin);

        System.out.println("✅ Admin seeded → username: " + username + " | password: " + rawPassword);
    }
}
