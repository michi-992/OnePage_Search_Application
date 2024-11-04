package org.topalovic.backend.service;


import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.topalovic.backend.payload.request.SignupRequest;
import org.topalovic.backend.payload.response.UserInfoResponse;

public interface AuthService {
    public UserInfoResponse signInUser(Authentication auth);
    public ResponseCookie signInCookie(Authentication auth);
    public String registerUser(SignupRequest signupRequest);
    public ResponseCookie logoutUser();
}
