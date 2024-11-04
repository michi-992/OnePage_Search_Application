package org.topalovic.backend.UserTests;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.topalovic.backend.controller.AuthController;
import org.topalovic.backend.payload.request.LoginRequest;
import org.topalovic.backend.payload.request.SignupRequest;
import org.topalovic.backend.payload.response.MessageResponse;
import org.topalovic.backend.payload.response.UserInfoResponse;
import org.topalovic.backend.service.AuthService;
import org.topalovic.backend.service.UserDetailsServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;


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

    @Test
    public void testAuthenticateUser_Success() {
        LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");
        Authentication authentication = mock(Authentication.class);
        UserInfoResponse userInfoResponse = new UserInfoResponse(1L, "testUser", "testUser@example.com", List.of("ROLE_USER"));
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "testJwt").build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authService.signInUser(authentication)).thenReturn(userInfoResponse);
        when(authService.signInCookie(authentication)).thenReturn(jwtCookie);

        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(jwtCookie.toString(), responseEntity.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
        assertEquals(userInfoResponse, responseEntity.getBody());
    }

    @Test
    public void testAuthenticateUser_BadCredentials() {
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenThrow(exception);

        assertThrows(BadCredentialsException.class, () -> authController.authenticateUser(loginRequest));
    }

}
