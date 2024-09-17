package com.project.notes_v2.service;

import com.project.notes_v2.dto.AccountNoteDTO;
import com.project.notes_v2.dto.AccountRequestDTO;
import com.project.notes_v2.dto.AccountResponseDTO;
import com.project.notes_v2.enumeration.Role;
import com.project.notes_v2.exception.AlreadyExistException;
import com.project.notes_v2.exception.DeleteException;
import com.project.notes_v2.exception.NotFoundException;
import com.project.notes_v2.exception.SaveException;
import com.project.notes_v2.exception.UnauthenticatedException;
import com.project.notes_v2.exception.UnauthorizedException;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.repository.AccountNoteRepository;
import com.project.notes_v2.repository.AccountRepository;
import com.project.notes_v2.repository.AccountSpecification;
import com.project.notes_v2.security.PasswordGenerator;
import com.project.notes_v2.mapper.AccountMapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Optional;
import java.time.Instant;


@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNoteRepository accountNoteRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HttpServletRequest httpServletRequest;
    private final AccountMapper accountMapper;
    private final EmailService emailService;
    private final PasswordGenerator passwordGenerator;

    public AccountService(AccountRepository accountRepository,
                          AccountNoteRepository accountNoteRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          HttpServletRequest httpServletRequest,
                          AccountMapper accountMapper,
                          EmailService emailService,
                          PasswordGenerator passwordGenerator) {
        this.accountRepository     = accountRepository;
        this.accountNoteRepository = accountNoteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.httpServletRequest    = httpServletRequest;
        this.accountMapper         = accountMapper;
        this.emailService          = emailService;
        this.passwordGenerator     = passwordGenerator;
    }

    /*---------------PUBLIC METHODS---------------*/

    public List<Account> getAccounts() {
        return this.accountRepository.findAll();
    }


    public List<Account> getAccountsByFilter(String email, String username, String firstname, String lastname, String role, String active) {
        List<Account> accounts = accountRepository.findAll(AccountSpecification.filterBy(email, username, firstname, lastname, role, active));
        return this.excludeAccountSession(accounts);
    }


    public List<AccountNoteDTO> getAccountByNoteId(int noteId) {
        return accountNoteRepository.findByNote_Id(noteId)
                .stream()
                .filter(accountNote -> accountNote.getAccount().getId() != this.getSessionUserId())
                .map(accountNote -> {
                    AccountNoteDTO accountNoteDTO = new AccountNoteDTO();
                    accountNoteDTO.setAccountId(accountNote.getAccount().getId());
                    accountNoteDTO.setNoteId(accountNote.getNote().getId());
                    accountNoteDTO.setUsername(accountNote.getAccount().getUsername());
                    accountNoteDTO.setRight(accountNote.getRight());
                    return accountNoteDTO;
                }).toList();
    }

    public AccountResponseDTO getAccount() {
        int accountId = getSessionUserId();
        Account account = this.accountRepository.findById(accountId)
                                                .orElseThrow( () -> new NotFoundException("Account not found: " + accountId));
        account.setModified(Instant.now());
        return this.accountMapper.toAccountResponseDTO(account);
    }


    public Account getAccount(Integer accountId) {
        return this.accountRepository.findById(accountId)
                                     .orElseThrow( () -> new NotFoundException("Account not found: " + accountId));
    }


    @Transactional
    public void createAccount(Account account) throws MessagingException {
        // Hash password
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        // Set datetime
        account.setCreated(Instant.now());
        // Set Role by default
        account.setRole(Role.USER);
        // Check email unique
        this.checkIfAccountWithEmailExists(account.getEmail());
        // Check username unique
        this.checkIfAccountWithUsernameExists(account.getUsername());
        // Save Account
        this.saveAccount(account);
        // Send Email Confirmation
        this.emailService.sendConfirmationEmail(account.getEmail(), account.getUsername(), true);
    }


    @Transactional
    public AccountResponseDTO updateAccount(AccountRequestDTO accountRequestDTO) throws MessagingException {
        // Get current Account id from session
        int accountId = this.getSessionUserId();
        // check user is allowed to update account
        if(!this.checkUserHasRight(accountId)) { throw new UnauthorizedException(); }

        return this.update(accountId, accountRequestDTO);
    }


    @Transactional
    public void updateAccount(int accountId, AccountRequestDTO accountRequestDTO) throws MessagingException {
        // check user is allowed to update account
        if(!this.checkUserHasRight(accountId)) { throw new UnauthorizedException(); }

        this.update(accountId, accountRequestDTO);
    }


    @Transactional
    public void deleteAccount(Integer accountId) {
        try {
            this.accountRepository.deleteById(accountId);
        } catch(DeleteException exception) {
            throw new DeleteException();

        }
    }

    public void recoverPassword(String email, String username) throws MessagingException {
        // Get account by email
        Optional<Account> accountOptional = this.accountRepository.findByEmail(email);
        if(accountOptional.isPresent()) {
            Account account = accountOptional.get();
            this.checkAccountEmailMatchUsername(account.getUsername(), username);
            // Create new random password
            String randomPassword = this.passwordGenerator.generateRandomPassword();
            // Save account with new password
            account.setPassword(bCryptPasswordEncoder.encode(randomPassword));
            this.saveAccount(account);
            // Send email
            this.emailService.sendLostPasswordEmail(account.getEmail(), randomPassword);
        } else {
            throw new NotFoundException("Account not found: " + email);
        }
    }

    /*---------------PRIVATE METHODS---------------*/

    private List<Account> excludeAccountSession(List<Account> accounts) {
        int userId = this.getSessionUserId();
        Account accountSession = this.getAccount(userId);
        accounts.remove(accountSession);
        return accounts;
    }

    private boolean checkUserHasRight(int accountId) {
        int userId = this.getSessionUserId();
        return userId == accountId || this.getAccount(userId).getRole() == Role.ADMIN;
    }

    private void checkAccountEmailMatchUsername(String email, String username) {
         if(!email.equals(username)) {
             throw new UnauthorizedException();
         }
    }

    private void checkIfAccountWithEmailExists(String email) {
        if(this.accountRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistException("Email " + email);
        }
    }

    private void checkIfAccountWithUsernameExists(String username) {
        if(this.accountRepository.findByUsername(username).isPresent()) {
            throw new AlreadyExistException("Username " + username);
        }
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


    private AccountResponseDTO update(int accountId, AccountRequestDTO accountRequestDTO) throws MessagingException {
        // Get Account to update
        Account accountToUpdate = this.getAccount(accountId);

        //Map from DTO
        if(!accountRequestDTO.getFirstname().isEmpty()) { accountToUpdate.setFirstname(accountRequestDTO.getFirstname()); }
        if(!accountRequestDTO.getLastname().isEmpty()) { accountToUpdate.setLastname(accountRequestDTO.getLastname()); }
        if(!accountRequestDTO.getUsername().isEmpty()) {
            // Check username still unique if updated
            if(!accountRequestDTO.getUsername().equals(accountToUpdate.getUsername())) {
                this.checkIfAccountWithUsernameExists(accountRequestDTO.getUsername());
            }
            accountToUpdate.setUsername(accountRequestDTO.getUsername());
        }
        if(!accountRequestDTO.getEmail().isEmpty()) {
            // Check email still unique if updated
            if(!accountRequestDTO.getEmail().equals(accountToUpdate.getEmail())) {
                this.checkIfAccountWithEmailExists(accountRequestDTO.getEmail());
            }
            accountToUpdate.setEmail(accountRequestDTO.getEmail());
        }
        if(StringUtils.hasLength(accountRequestDTO.getPassword())) {
            accountToUpdate.setPassword(bCryptPasswordEncoder.encode(accountRequestDTO.getPassword()));
        }
        accountToUpdate.setModified(Instant.now());
        accountToUpdate.setIsDevMode(accountRequestDTO.getIsDevMode());
        accountToUpdate.setIsToolTips(accountRequestDTO.getIsToolTips());
        accountToUpdate.setIsEditable(accountRequestDTO.getIsEditable());

        // Save Account
        Account accountSaved = this.saveAccount(accountToUpdate);

        // Send Email Confirmation
        this.emailService.sendConfirmationEmail(accountSaved.getEmail(), accountSaved.getUsername(), false);

        // Map to DTO using MapStruct
        return this.accountMapper.toAccountResponseDTO(accountSaved);
    }



}
