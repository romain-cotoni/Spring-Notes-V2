package com.project.notes_v2.service;

import com.project.notes_v2.dto.AccountDTO;
import com.project.notes_v2.enumeration.Role;
import com.project.notes_v2.exception.DeleteException;
import com.project.notes_v2.exception.NotFoundException;
import com.project.notes_v2.exception.SaveException;
import com.project.notes_v2.exception.UnauthenticatedException;
import com.project.notes_v2.exception.UnauthorizedException;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.repository.AccountNoteRepository;
import com.project.notes_v2.repository.AccountRepository;
import com.project.notes_v2.repository.AccountSpecification;
import com.project.notes_v2.repository.NoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final NoteRepository noteRepository;
    private final AccountNoteRepository accountNoteRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HttpServletRequest httpServletRequest;

    public AccountService(AccountRepository accountRepository,
                          NoteRepository noteRepository,
                          AccountNoteRepository accountNoteRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          HttpServletRequest httpServletRequest) {
        this.accountRepository = accountRepository;
        this.noteRepository = noteRepository;
        this.accountNoteRepository = accountNoteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.httpServletRequest = httpServletRequest;
    }

    /*---------------PUBLIC METHODS---------------*/

    public List<Account> getAccounts() {
        return this.accountRepository.findAll();
    }

    public List<Account> getAccountsByFilter(String email, String username, String firstname, String lastname, String role, String active) {
        return accountRepository.findAll(AccountSpecification.filterBy(email, username, firstname, lastname, role, active));
    }

    public Account getAccount(Integer accountId) {
        return this.accountRepository.findById(accountId)
                                     .orElseThrow( () -> new NotFoundException("Account not found: " + accountId));
    }

    @Transactional
    public void createAccount(Account account) {
        // hash password
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        // set datetime
        account.setCreated(Instant.now());
        // save Account
        this.saveAccount(account);
    }

    @Transactional
    public void updateAccount(int accountId, AccountDTO accountDTO) {
        // check user is allowed to update account
        if(!this.checkUserHasRight(accountId)) { throw new UnauthorizedException(); }
        // get Account to update
        Account accountToUpdate = this.getAccount(accountId);
        // update Account //TODO: mapping with mapstruct
        if(!accountDTO.getEmail().isEmpty()) {
            accountToUpdate.setEmail(accountDTO.getEmail());
        }
        if(accountDTO.getActive() != null) {
            accountToUpdate.setActive(accountDTO.getActive());
        }
        // set datetime of modification
        accountToUpdate.setModified(Instant.now());
        // save Account
        this.saveAccount(accountToUpdate);
    }

    @Transactional
    public void deleteAccount(Integer accountId) {
        try {
            this.accountRepository.deleteById(accountId);
        } catch(DeleteException exception) {
            throw new DeleteException();

        }
    }

    /*---------------PRIVATE METHODS---------------*/

    private boolean checkUserHasRight(int accountId) {
        int userId = this.getSessionUserId();
        return userId == accountId ||
               this.getAccount(userId).getRole() == Role.ADMIN;
    }

    private Account saveAccount(Account account) {
        try {
            return this.accountRepository.save(account);
        } catch(SaveException exception) {
            throw new SaveException();
        }
    }

    private int getSessionUserId() {
        try {
            HttpSession session = httpServletRequest.getSession();
            return (Integer) session.getAttribute("sessionUserId");
        } catch(UnauthenticatedException exception) {
            throw new UnauthenticatedException();
        }
    }

}
