package org.topalovic.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.topalovic.backend.model.ERole;
import org.topalovic.backend.model.Role;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.RoleRepository;
import org.topalovic.backend.repository.SearchItemRepository;
import org.topalovic.backend.repository.UserRepository;

import java.util.Set;

@Component
public class DataInitConfig {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, SearchItemRepository searchItemRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_USER));
            }
            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_ADMIN));
            }

            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();

            UserProfile user1;
            String username1 = "user1";
            if (!userRepository.existsByUsername(username1)) {
                user1 = new UserProfile(username1, "user1@example.com", passwordEncoder.encode("password1"));
                user1.setRoles(Set.of(userRole));
                userRepository.save(user1);
            } else {
                user1 = userRepository.findByUsername(username1).orElseThrow();
            }

            // Create Admin User with ADMIN and USER roles
            UserProfile admin;
            String username2 = "admin";
            if (!userRepository.existsByUsername(username2)) {
                admin = new UserProfile(username2, "admin@example.com", passwordEncoder.encode("password2"));
                admin.setRoles(Set.of(adminRole, userRole));
                userRepository.save(admin);
            } else {
                admin = userRepository.findByUsername(username2).orElseThrow();
            }

            // Create two SearchItems for each user
            if (searchItemRepository.findByUser(user1).isEmpty()) {
                SearchItem user1Item1 = new SearchItem("Search term 1 for user1", user1);
                SearchItem user1Item2 = new SearchItem("Search term 2 for user1", user1);
                searchItemRepository.save(user1Item1);
                searchItemRepository.save(user1Item2);
            }

            if (searchItemRepository.findByUser(admin).isEmpty()) {
                SearchItem adminItem1 = new SearchItem("Search term 1 for admin", admin);
                SearchItem adminItem2 = new SearchItem("Search term 2 for admin", admin);
                searchItemRepository.save(adminItem1);
                searchItemRepository.save(adminItem2);
            }
        };
    }
}
