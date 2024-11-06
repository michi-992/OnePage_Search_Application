package org.topalovic.backend.SearchItemTests;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.topalovic.backend.model.ERole;
import org.topalovic.backend.model.Role;
import org.topalovic.backend.model.SearchItem;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.repository.RoleRepository;
import org.topalovic.backend.repository.SearchItemRepository;
import org.topalovic.backend.repository.UserRepository;
import org.topalovic.backend.service.SearchItemService;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class SearchItemIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Autowired
    private SearchItemService searchItemService;

    @Autowired
    private SearchItemRepository searchItemRepo;

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

        if (searchItemRepo.findByUser(normalUser).isEmpty()) {
            SearchItem user1Item1 = new SearchItem("Search term 1 for user1", normalUser);
            SearchItem user1Item2 = new SearchItem("Search term 2 for user1", normalUser);
            searchItemRepo.save(user1Item1);
            searchItemRepo.save(user1Item2);
        }

        if (searchItemRepo.findByUser(adminUser).isEmpty()) {
            SearchItem adminItem1 = new SearchItem("Search term 1 for admin", adminUser);
            SearchItem adminItem2 = new SearchItem("Search term 2 for admin", adminUser);
            searchItemRepo.save(adminItem1);
            searchItemRepo.save(adminItem2);
        }
    }

    @Test
    // @Transactional
    public void testAddRealSearchItem() {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("integration test item");
        searchItem.setUser(normalUser);

        SearchItem savedItem = searchItemService.addSearchItem(searchItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getSearchTerm()).isEqualTo("integration test item");
        assertThat(savedItem.getUser()).isEqualTo(normalUser);

        Optional<SearchItem> retrievedItem = searchItemRepo.findById(savedItem.getId());
        assertThat(retrievedItem).isPresent();
        assertThat(retrievedItem.get().getSearchTerm()).isEqualTo("integration test item");
        assertThat(retrievedItem.get().getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    @WithMockUser
    // @Transactional
    public void testAddRealSearchItemEndpoint() throws Exception {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("integration test item - web");
        searchItem.setUser(userRepository.findByUsername("user1").orElseThrow());

        String json = "{\"searchTerm\": \"" + searchItem.getSearchTerm() + "\", \"user\": {\"id\": " + searchItem.getUser().getId() +
                ", \"username\": \"" + searchItem.getUser().getUsername() + "\"}}";

        ResultActions resultActions = mockMvc.perform(post("/api/searchItems/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchTerm").value(searchItem.getSearchTerm()))
                .andExpect(jsonPath("$.user.id").value(searchItem.getUser().getId()));
    }

    @Test
    @WithMockUser
    public void testAddSearchItemWithInvalidUser() throws Exception {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("integration test item - invalid user");
        searchItem.setUser(new UserProfile("nonexistentuser", "nonexistentuser@example.com", "password"));

        String json = "{\"searchTerm\": \"" + searchItem.getSearchTerm() + "\", \"user\": {\"id\": " + searchItem.getUser().getId() +
                ", \"username\": \"" + searchItem.getUser().getUsername() + "\"}}";

        ResultActions resultActions = mockMvc.perform(post("/api/searchItems/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andExpect(status().isNotFound()).andExpect(result -> assertEquals("User not found with username: nonexistentuser",
                result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser
    public void testAddSearchItemWithBlankSearchTerm() throws Exception {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("");
        searchItem.setUser(normalUser);

        String json = "{\"searchTerm\": \"" + searchItem.getSearchTerm() + "\", \"user\":{\"id\": " + searchItem.getUser().getId() +
                ", \"username\": \"" + searchItem.getUser().getUsername() + "\"}}";

        ResultActions resultActions = mockMvc.perform(post("/api/searchItems/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testAddSearchItemWithNullSearchTerm() throws Exception {
        SearchItem searchItem = new SearchItem();
        searchItem.setUser(normalUser);

        String json = "{\"user\":{\"id\": " + searchItem.getUser().getId() +
                ", \"username\": \"" + searchItem.getUser().getUsername() + "\"}}";

        ResultActions resultActions = mockMvc.perform(post("/api/searchItems/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testAddSearchItemWithNullUser() throws Exception {
        SearchItem searchItem = new SearchItem();
        searchItem.setSearchTerm("integration test item - null user");
        searchItem.setUser(null);

        String json = "{\"searchTerm\": \"" + searchItem.getSearchTerm() + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/searchItems/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user1")
    public void getSearchItemsByUser() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/searchItems/user/{username}", normalUser.getUsername()));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].user.id").value(normalUser.getId()))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].searchTerm").exists())
                .andExpect(jsonPath("$[0].searchedAt").exists())
                .andExpect(jsonPath("$[0].user.id").exists());
    }

    @Test
    @WithMockUser
    public void getSearchItemsByNonExistentUser() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/searchItems/user/{username}", "nonexistentuser"));

        resultActions.andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("User not found with username: nonexistentuser",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithUserDetails("admin")
    public void getAllSearchItemsByAdmin() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/searchItems"));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].searchTerm").exists())
                .andExpect(jsonPath("$[0].searchedAt").exists())
                .andExpect(jsonPath("$[0].user.id").exists());
    }
}
