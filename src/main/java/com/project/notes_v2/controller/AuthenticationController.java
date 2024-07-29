package com.project.notes_v2.controller;

import com.project.notes_v2.exception.FailedRequestException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/authentication")
public class AuthenticationController {
    //private final AuthenticationManager authenticationManager;
    //private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /*public AuthenticationController(AuthenticationManager authenticationManager,
                                    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.authenticationManager = authenticationManager;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }*/

    /*@PostMapping(value = "/login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody AuthenticationDTO authenticationDTO) {
        try {
            return ResponseEntity.ok("Login successful");
        } catch(RuntimeException exception) {
            System.out.println(exception);
            return ResponseEntity.ok("Login unsuccessful");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("You are been logged out");
    }*/

    @GetMapping("/successLogin")
    public String getSuccessLogin() {
        return "You are successfully connected :)";
    }

    @GetMapping("/successLogout")
    public String getSuccessLogout() {
        return "You are successfully disconnected :)";
    }

}
