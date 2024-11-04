package org.topalovic.backend.UserTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.topalovic.backend.security.services.UserDetailsServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    
    @Test
    public void testPublicAccessToTestEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testUserAccess() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAdminAccess() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testUserCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin"))
                .andExpect(status().isForbidden());
    }

}
