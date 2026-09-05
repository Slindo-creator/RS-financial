package com.afrofuturists.rsfinancial.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.afrofuturists.rsfinancial.domain.StaffRole;
import com.afrofuturists.rsfinancial.domain.StaffUser;
import com.afrofuturists.rsfinancial.repository.StaffUserRepository;

/**
 * Seeds a couple of staff accounts on startup, for local development
 * only - replaces the old DataInitializer, which tried to create users
 * via JdbcUserDetailsManager against a `users`/`authorities` schema that
 * was never created. This seeds through the real StaffUserRepository
 * instead, using the actual role model (ADVISER/COMPLIANCE_OFFICER/
 * ADMIN), not Spring Security's generic USER/ADMIN demo roles.
 *
 * These are placeholder credentials for local dev convenience - real
 * staff accounts should be created through an admin-only provisioning
 * path once one exists, not left seeded in code past initial setup.
 */
@Component
public class StaffUserSeeder implements CommandLineRunner {

    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffUserSeeder(StaffUserRepository staffUserRepository, PasswordEncoder passwordEncoder) {
        this.staffUserRepository = staffUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedIfMissing("adviser1", "Test Adviser", StaffRole.ADVISER, "changeme1");
        seedIfMissing("compliance1", "Test Compliance Officer", StaffRole.COMPLIANCE_OFFICER, "changeme2");
        seedIfMissing("admin", "Test Admin", StaffRole.ADMIN, "changeme3");
    }

    private void seedIfMissing(String username, String fullName, StaffRole role, String rawPassword) {
        if (staffUserRepository.existsByUsername(username)) {
            return;
        }
        StaffUser user = StaffUser.builder()
                .username(username)
                .fullName(fullName)
                .role(role)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .enabled(true)
                .build();
        staffUserRepository.save(user);
    }
}
