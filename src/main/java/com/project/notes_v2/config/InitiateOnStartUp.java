package com.project.notes_v2.config;

import com.project.notes_v2.enumeration.Right;
import com.project.notes_v2.enumeration.Role;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.model.Note;
import com.project.notes_v2.repository.AccountNoteRepository;
import com.project.notes_v2.repository.AccountRepository;
import com.project.notes_v2.repository.NoteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class InitiateOnStartUp {
    private final NoteRepository noteRepository;

    private final AccountRepository accountRepository;

    private final AccountNoteRepository accountNoteRepository;

    private final BCryptPasswordEncoder bcryptEncoder;


    @PostConstruct
    private void runAfterStartup() {

        System.out.println("--InitiateOnStartUp - Creation of fixtures--");

        // create account 1
        Account account1 = new Account();
        account1.setFirstname("romain");
        account1.setLastname("cotoni");
        account1.setEmail("romain.cotoni.2@gmail.com");
        account1.setUsername("rom1");
        account1.setPassword(bcryptEncoder.encode("ssap"));
        account1.setRole(Role.ADMIN);
        account1.setCreated(Instant.now());
        account1 = accountRepository.save(account1);

        // create account 2
        Account account2 = new Account();
        account2.setEmail("romain.cotoni@gmail.com");
        account2.setUsername("rom2");
        account2.setPassword(bcryptEncoder.encode("ssap"));
        account2.setRole(Role.USER);
        account2.setCreated(Instant.now());
        accountRepository.save(account2);

        // create account 3
        Account account3 = new Account();
        account3.setEmail("rom3@mail.com");
        account3.setUsername("rom3");
        account3.setPassword(bcryptEncoder.encode("ssap"));
        account3.setRole(Role.USER);
        account3.setCreated(Instant.now());
        accountRepository.save(account3);

        // create account 4
        Account account4 = new Account();
        account4.setEmail("tom1@mail.com");
        account4.setUsername("tom1");
        account4.setPassword(bcryptEncoder.encode("ssap"));
        account4.setRole(Role.USER);
        account4.setCreated(Instant.now());
        accountRepository.save(account4);

        // create account 5
        Account account5 = new Account();
        account5.setEmail("tom2@mail.com");
        account5.setUsername("tom2");
        account5.setPassword(bcryptEncoder.encode("ssap"));
        account5.setRole(Role.USER);
        account5.setCreated(Instant.now());
        accountRepository.save(account5);

        // create note 1
        Note note1 = new Note();
        note1.setTitle("note 1 title");
        note1.setContent("<h1>hello world rom1</h1>" +
                         "<p>TODO: </p>" +
                         "<p>Backend custom enum & password validators.</p>" +
                         "<p>Make a note public.</p>" +
                         "<p>Confirm email to finalise registration.</p>" +
                         "<p>Add send email for each notification and display them in Header & Profil.</p>" +
                         "<p>Translation French English.</p>" +
                         "<p>Develop isDevMode only for ADMIN.</p>");
        note1.setCreated(Instant.now());
        note1 = noteRepository.save(note1);

        // create note 2
        Note note2 = new Note();
        note2.setTitle("note 2 title");
        note2.setContent("hello world rom2");
        note2.setCreated(Instant.now());
        note2 = noteRepository.save(note2);

        // create note 3
        Note note3 = new Note();
        note3.setTitle("note 3 title");
        note3.setContent("hello world rom3");
        note3.setCreated(Instant.now());
        note3 = noteRepository.save(note3);

        // associate Note 1
        accountNoteRepository.save(new AccountNote(account1, note1, Right.OWNER));
        accountNoteRepository.save(new AccountNote(account2, note1, Right.WRITE));
        // associate Note 2
        accountNoteRepository.save(new AccountNote(account1, note2, Right.OWNER));
        // associate Note 3
        accountNoteRepository.save(new AccountNote(account1, note3, Right.OWNER));


        System.out.println("--------------------------------------------");
    }

}
