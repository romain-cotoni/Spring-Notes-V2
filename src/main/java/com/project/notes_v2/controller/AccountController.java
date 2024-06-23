package com.project.notes_v2.controller;

import com.project.notes_v2.dto.AccountDTO;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.service.AccountService;
import com.project.notes_v2.exception.FailedRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts() {
        try {
            List<Account> accounts = this.accountService.getAccounts();
            return ResponseEntity.ok(accounts);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Account>> getAccountsByFilter(@RequestParam(name="email", required=false) String email,
                                                             @RequestParam(name="username", required=false) String username,
                                                             @RequestParam(name="firstname", required=false) String firstname,
                                                             @RequestParam(name="lastname", required=false) String lastname,
                                                             @RequestParam(name="role", required=false) String role,
                                                             @RequestParam(name="active", required=false) String active) {
        try {
            List<Account> accounts = this.accountService.getAccountsByFilter(email, username, firstname, lastname, role, active);
            return ResponseEntity.ok(accounts);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable int accountId) {
        try {
            Account account = this.accountService.getAccount(accountId);
            return ResponseEntity.ok(account);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> createAccount(@RequestBody Account account) {
        try {
            this.accountService.createAccount(account);
            return ResponseEntity.ok(true);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<Boolean> updateAccount(@PathVariable int accountId, @RequestBody AccountDTO accountDTORequest) {
        try {
            this.accountService.updateAccount(accountId, accountDTORequest);
            return ResponseEntity.ok(true);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Boolean> deleteAccount(@PathVariable int accountId) {
        try {
            this.accountService.deleteAccount(accountId);
            return ResponseEntity.ok(true);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

}
