package org.topalovic.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.topalovic.backend.model.ERole;
import org.topalovic.backend.model.Role;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.RoleRepository;
import org.topalovic.backend.repository.UserRepository;

import java.util.Set;

@Component
public class DataInitConfig {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_USER));
            }
            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_ADMIN));
            }

            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();

            String username1 = "user1";
            if (!userRepository.existsByUsername(username1)) {
                UserProfile user1 = new UserProfile(username1, "user1@example.com", passwordEncoder.encode("password1"));
                user1.setRoles(Set.of(userRole));
                userRepository.save(user1);
            }

            String username2 = "admin";
            if (!userRepository.existsByUsername(username2)) {
                UserProfile admin = new UserProfile(username2, "admin@example.com", passwordEncoder.encode("password2"));
                admin.setRoles(Set.of(adminRole, userRole));  // Admin has both roles
                userRepository.save(admin);
            }
        };
    }
}
