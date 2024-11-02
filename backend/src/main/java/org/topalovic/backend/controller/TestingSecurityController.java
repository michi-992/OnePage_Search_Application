package org.topalovic.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestingSecurityController {

    @GetMapping("/")
    public String home() {
        return "Home Page";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Home Admin";
    }
}
