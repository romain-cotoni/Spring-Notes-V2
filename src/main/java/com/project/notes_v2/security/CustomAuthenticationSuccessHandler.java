package com.project.notes_v2.security;
import com.project.notes_v2.exception.NotFoundException;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.repository.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component("customAuthSuccessHandler")
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final AccountRepository accountRepository;

    public CustomAuthenticationSuccessHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Account> OptionlAccount = this.accountRepository.findByUsername(userDetails.getUsername());
        int sessionUserId = Optional.ofNullable(this.accountRepository.findByUsername(userDetails.getUsername()))
                                    .map(account -> account.get().getId())
                                    .orElseThrow( () -> new NotFoundException("Account not found - username: " + userDetails.getUsername()));
        HttpSession session = request.getSession();
        session.setAttribute("sessionUserId", sessionUserId);
        response.sendRedirect("/api/authentication/successLogin");
    }


}
