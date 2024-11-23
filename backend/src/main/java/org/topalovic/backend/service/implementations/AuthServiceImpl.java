package org.topalovic.backend.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.topalovic.backend.model.ERole;
import org.topalovic.backend.model.Role;
import org.topalovic.backend.model.UserProfile;
import org.topalovic.backend.payload.request.SignupRequest;
import org.topalovic.backend.payload.response.UserInfoResponse;
import org.topalovic.backend.repository.RoleRepository;
import org.topalovic.backend.repository.UserRepository;
import org.topalovic.backend.config.utility.JwtUtils;
import org.topalovic.backend.service.AuthService;
import org.topalovic.backend.model.UserDetailsCustom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    public UserInfoResponse signInUser(Authentication auth) {

        SecurityContextHolder.getContext().setAuthentication(auth);

        UserDetailsCustom userDetails = (UserDetailsCustom) auth.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    public ResponseCookie signInCookie(Authentication auth) {
        UserDetailsCustom userDetails = (UserDetailsCustom) auth.getPrincipal();
        return jwtUtils.generateJwtCookie(userDetails);
    }

    public String registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return "Error: Username is already taken!";
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return "Error: Email is already in use!";
        }

        UserProfile user = new UserProfile(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return "User registered successfully!";
    }

    public ResponseCookie logoutUser() {
        return jwtUtils.getCleanJwtCookie();
    }
}
