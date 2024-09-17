package com.project.notes_v2.controller;

import com.project.notes_v2.dto.AccountResponseDTO;
import com.project.notes_v2.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/authentication")
public class AuthenticationController {

    private final AccountService accountService;

    @GetMapping("/successLogin")
    public AccountResponseDTO getSuccessLogin() {
        return accountService.getAccount();
    }

    @GetMapping("/successLogout")
    public String getSuccessLogout() {
        return "You are successfully disconnected :)";
    }

}
