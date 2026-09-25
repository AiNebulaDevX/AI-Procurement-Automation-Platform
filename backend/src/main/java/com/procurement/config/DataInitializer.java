package com.procurement.config;

import com.procurement.entity.User;
import com.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing default users...");
            
            User admin = User.builder()
                    .username("admin")
                    .email("admin@procurement.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .department("IT")
                    .build();
            
            User manager = User.builder()
                    .username("manager")
                    .email("manager@procurement.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.PROCUREMENT_MANAGER)
                    .department("Procurement")
                    .build();
            
            User finance = User.builder()
                    .username("finance")
                    .email("finance@procurement.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.FINANCE_APPROVER)
                    .department("Finance")
                    .build();
            
            User viewer = User.builder()
                    .username("viewer")
                    .email("viewer@procurement.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.VIEWER)
                    .department("Operations")
                    .build();
            
            userRepository.save(admin);
            userRepository.save(manager);
            userRepository.save(finance);
            userRepository.save(viewer);
            
            log.info("Default users initialized successfully");
        } else {
            log.info("Users already exist, skipping initialization");
        }
    }
}