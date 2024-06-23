package com.project.notes_v2.controller;

import com.project.notes_v2.dto.AuthenticationDTO;
import com.project.notes_v2.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/authentication")
public class AuthenticationController {

    private final AccountService accountService;

    public AuthenticationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody AuthenticationDTO authenticationDTO) {
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        return "You are been logged out";
    }

    @GetMapping("/successLogin")
    public String getSuccessLogin() {
        return "You are successfully connected :)";
    }

    @GetMapping("/successLogout")
    public String getSuccessLogout() {
        return "You are successfully disconnected :)";
    }

    @GetMapping("/user")
    public String getUser() {
        return "Welcome, User";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Welcome, Admin";
    }

}
