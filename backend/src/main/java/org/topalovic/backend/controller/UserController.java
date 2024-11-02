package org.topalovic.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    // already configured in SecurityConfig
    @GetMapping("/user")
    public String user() {
        return "This is accessible by any user.";
    }

    // already configured in SecurityConfig
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "This is only accessible by an admin.";
    }
}
