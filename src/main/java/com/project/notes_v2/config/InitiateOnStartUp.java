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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InitiateOnStartUp {
    private final NoteRepository noteRepository;

    private final AccountRepository accountRepository;

    private final AccountNoteRepository accountNoteRepository;

    private final BCryptPasswordEncoder bcryptEncoder;

    public InitiateOnStartUp(NoteRepository noteRepository,
                             AccountRepository accountRepository,
                             AccountNoteRepository accountNoteRepository,
                             BCryptPasswordEncoder bcryptEncoder) {
        this.noteRepository = noteRepository;
        this.accountRepository = accountRepository;
        this.accountNoteRepository = accountNoteRepository;
        this.bcryptEncoder  = bcryptEncoder;
    }

    @PostConstruct
    private void runAfterStartup() {
        System.out.println("---------------InitiateOnStartUp - Creation of fixtures---------------");

        // create account 1
        Account account1 = new Account();
        account1.setEmail("rom1@mail.com");
        account1.setUsername("rom1");
        account1.setPassword(bcryptEncoder.encode("ssap"));
        account1.setRole(Role.USER);
        account1 = accountRepository.save(account1);

        // create account 2
        Account account2 = new Account();
        account2.setEmail("rom2@mail.com");
        account2.setUsername("rom2");
        account2.setPassword(bcryptEncoder.encode("ssap"));
        account2.setRole(Role.USER);
        account2 = accountRepository.save(account2);

        // create note 1
        Note note1 = new Note();
        note1.setTitle("note 1 title");
        note1.setContent("hello world rom1");
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
        // associate Note 2
        accountNoteRepository.save(new AccountNote(account1, note2, Right.OWNER));
        accountNoteRepository.save(new AccountNote(account2, note2, Right.WRITE));
        // associate Note 3
        accountNoteRepository.save(new AccountNote(account1, note3, Right.WRITE));

    }
}
