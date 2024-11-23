package org.topalovic.backend.UserTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.topalovic.backend.model.ERole;
import org.topalovic.backend.model.Role;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.RoleRepository;
import org.topalovic.backend.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@WebAppConfiguration
public class UserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserProfile normalUser;
    private UserProfile adminUser;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow();

        String username1 = "user1";
        if (!userRepository.existsByUsername(username1)) {
            UserProfile user1 = new UserProfile(username1, "user1@example.com", passwordEncoder.encode("password1"));
            user1.setRoles(Set.of(userRole));
            normalUser = userRepository.save(user1);
        } else {
            normalUser = userRepository.findByUsername("user1").orElseThrow();
        }

        String username2 = "admin";
        if (!userRepository.existsByUsername(username2)) {
            UserProfile admin = new UserProfile(username2, "admin@example.com", passwordEncoder.encode("password2"));
            admin.setRoles(Set.of(adminRole, userRole));
            adminUser = userRepository.save(admin);
        } else {
            adminUser = userRepository.findByUsername("admin").orElseThrow();
        }
    }

    @Test
    @Transactional
    public void testRegisterUser() {
        UserProfile user = new UserProfile("username1", "username1@example", "password1");
        user.setRoles(new HashSet<>(Set.of(new Role(ERole.ROLE_USER))));

        UserProfile registeredUser  = userRepository.save(user);

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(registeredUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(registeredUser.getPassword()).isEqualTo(user.getPassword());

        Optional<UserProfile> retrievedUser = userRepository.findById(registeredUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(retrievedUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(retrievedUser.get().getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @Transactional
    public void testRegisterUserEndpoint() throws Exception {
        UserProfile user = new UserProfile("username2", "username2@example", "password1");
        user.setRoles(new HashSet<>(Set.of(new Role(ERole.ROLE_USER))));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
