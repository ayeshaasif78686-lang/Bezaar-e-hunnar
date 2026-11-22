package com.bazaarehunar.config;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.Role;
import com.bazaarehunar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminName;
    private final boolean resetPassword;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.admin.email}") String adminEmail,
                       @Value("${app.admin.password}") String adminPassword,
                       @Value("${app.admin.name}") String adminName,
                       @Value("${app.admin.reset-password:false}") boolean resetPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
        this.resetPassword = resetPassword;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            // Keep the configured admin able to sign in, and optionally reset its password. A normal customer/seller
            // account that happens to use this e-mail is never promoted to admin.
            userRepository.findByEmailIgnoreCase(normalizedEmail).filter(user -> user.getRole() == Role.ADMIN).ifPresent(existing -> {
                boolean changed = false;
                if (!existing.isActive()) { existing.setActive(true); changed = true; }
                if (resetPassword) {
                    existing.setPassword(passwordEncoder.encode(adminPassword));
                    changed = true;
                    System.out.println("Admin password reset from ADMIN_PASSWORD: " + normalizedEmail);
                }
                if (changed) userRepository.save(existing);
            });
        } else {
            AppUser admin = new AppUser(
                    adminName,
                    normalizedEmail,
                    passwordEncoder.encode(adminPassword),
                    Role.ADMIN
            );
            userRepository.save(admin);
            System.out.println("Default admin created: " + normalizedEmail);
        }
    }
}
