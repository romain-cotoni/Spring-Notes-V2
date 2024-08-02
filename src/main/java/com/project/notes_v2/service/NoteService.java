package com.project.notes_v2.service;

import com.project.notes_v2.dto.AccountNoteDTO;
import com.project.notes_v2.enumeration.Right;
import com.project.notes_v2.exception.AssociationException;
import com.project.notes_v2.exception.NotFoundException;
import com.project.notes_v2.exception.DeleteException;
import com.project.notes_v2.exception.SaveException;
import com.project.notes_v2.exception.ShareException;
import com.project.notes_v2.exception.UnauthenticatedException;
import com.project.notes_v2.exception.UnauthorizedException;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.model.AccountNoteId;
import com.project.notes_v2.model.Note;
import com.project.notes_v2.dto.NoteDTO;
import com.project.notes_v2.repository.AccountNoteRepository;
import com.project.notes_v2.repository.AccountRepository;
import com.project.notes_v2.repository.NoteRepository;
import com.project.notes_v2.repository.NoteSpecification;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final AccountRepository accountRepository;
    private final AccountNoteRepository accountNoteRepository;
    private final HttpServletRequest httpServletRequest;


    /*---------------Constructors---------------*/

    public NoteService(HttpServletRequest httpServletRequest,
                       NoteRepository noteRepository,
                       AccountRepository accountRepository,
                       AccountNoteRepository accountNoteRepository) {
        this.httpServletRequest = httpServletRequest;
        this.noteRepository = noteRepository;
        this.accountRepository = accountRepository;
        this.accountNoteRepository = accountNoteRepository;
    }


    /*---------------PUBLIC METHODS---------------*/

    public List<Note> getNotes() {
        return accountNoteRepository.findByAccount_Id(getSessionUserId())
                                    .stream()
                                    .map(AccountNote::getNote)
                                    .toList();
    }

    /*public List<Note> getNotesByFilter(String title, String content, String right) {
        List<AccountNote> accountNotes = accountNoteRepository.findAll(AccountNoteSpecification.filterBy(getSessionUserId(),title,content,right));
        List<Note> notes = accountNotes.stream()
                                       .map(AccountNote::getNote)
                                       .toList();
        return notes;
    }*/

    public List<Note> getNotesByFilter(String title, String content, String right) {
        List<Note> notes = noteRepository.findAll(NoteSpecification.filterBy(getSessionUserId(),title,content,right));
        return notes;
    }

    public Note getNote(int noteId) {
        AccountNote accountNote = accountNoteRepository.findById(new AccountNoteId(getSessionUserId(), noteId))
                                                       .orElseThrow(() -> new NotFoundException());
        return accountNote.getNote();
    }

    @Transactional
    public Note createNote(NoteDTO noteDTO) {
        // map noteToCreate with noteDTO
        Note noteToCreate = new Note();
        noteToCreate.setTitle(StringUtils.hasText(noteDTO.getTitle())  ? noteDTO.getTitle() : "New note");// set title if empty
        if(noteDTO.getContent() != null) {
            noteToCreate.setContent(noteDTO.getContent());
        }
        if(noteDTO.getImages() != null && !noteDTO.getImages().isEmpty()) {
            noteToCreate.setImages(noteDTO.getImages());
        }
        if(noteDTO.getLinks() != null && !noteDTO.getLinks().isEmpty()) {
            noteToCreate.setLinks(noteDTO.getLinks());
        }
        // set datetime
        noteToCreate.setCreated(Instant.now());
        // save Note in db
        Note noteSaved = this.saveNote(noteToCreate);
        // set AccountNote association
        AccountNote accountNote = this.setAccountNote(getSessionUserId(), noteSaved.getId(), Right.OWNER);
        // save AccountNote association
        this.saveAccountNote(accountNote);

        return noteSaved;
    }

    @Transactional
    public Note updateNote(int noteId, NoteDTO noteDTO) {
        // check user is allowed to update note
        if(!this.checkUserHasRight(Right.WRITE, noteId)) { throw new UnauthorizedException(); }
        // get note to update
        Note noteToUpdate = this.getNote(noteId);
        // update note
        if(StringUtils.hasLength(noteDTO.getTitle())) {  //!noteDTO.getTitle().isEmpty())
            noteToUpdate.setTitle(noteDTO.getTitle());
        }
        if(StringUtils.hasLength(noteDTO.getContent())) { // noteDTO.getContent() != null && !noteDTO.getContent().isEmpty()
            noteToUpdate.setContent(noteDTO.getContent());
        }
        if(noteDTO.getImages() != null && !noteDTO.getImages().isEmpty()) {
            noteToUpdate.setImages(noteDTO.getImages());
        }
        if(noteDTO.getLinks() != null && !noteDTO.getLinks().isEmpty()) {
            noteToUpdate.setLinks(noteDTO.getLinks());
        }
        if(noteDTO.getShare() != null) { noteToUpdate.setShare(noteDTO.getShare()); }
        // set the datetime of modification
        noteToUpdate.setModified(Instant.now());
        // save note
        return this.saveNote(noteToUpdate);
    }

    @Transactional
    public void eraseNote(int noteId) {
        // check user is allowed to delete note
        if(!this.checkUserHasRight(Right.DELETE, noteId)) { throw new UnauthorizedException(); }
        // get all AccountNote association with NOTE been deleted
        List<AccountNote> accountNotesToDeleteList = this.getAccountNoteByNoteId(noteId);
        //remove from all accounts associated
        accountNotesToDeleteList.forEach(accountNote -> {
            this.deleteAccountNote(new AccountNoteId(accountNote.getAccount().getId(), noteId));
        });
        //delete Note
        this.deleteNote(noteId);
    }

    @Transactional
    public void share(AccountNoteDTO accountNoteDTO) {
        // check current user Account has rights to share Note
        if(!this.checkUserHasRight(Right.SHARE, accountNoteDTO.getNoteId())) { throw new UnauthorizedException(); }
        // check AccountNote association doesn't already exist
        if(this.checkAssociationExist( accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId() ) ) {
            throw new ShareException();
        }
        // map AccountNote to share
        AccountNote accountNoteToShare = this.setAccountNote(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId(), accountNoteDTO.getRight());
        // Associate Account and Note
        this.saveAccountNote(accountNoteToShare);
    }

    @Transactional
    public void updateShare(AccountNoteDTO accountNoteDTO) {
        // check current user Account has rights to share Note
        if(!this.checkUserHasRight(Right.SHARE, accountNoteDTO.getNoteId())) { throw new UnauthorizedException(); }
        // get AccountNote to update
        AccountNote accountNoteToUpdate = this.getAccountNote(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId());
        // update AccountNote
        if(accountNoteDTO.getRight() != null) { accountNoteToUpdate.setRight(accountNoteDTO.getRight()); }
        // save AccountNote association in db
        saveAccountNote(accountNoteToUpdate);
    }

    @Transactional
    public void unshare(AccountNoteDTO accountNoteDTO) {
        // check current user Account has rights to unshare Note
        if(!this.checkUserHasRight(Right.SHARE, accountNoteDTO.getNoteId())) { throw new UnauthorizedException(); }
        // delete AccountNote association from the db
        this.deleteAccountNote(new AccountNoteId(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId()));
    }


    /*---------------PRIVATE METHODS---------------*/

    private Note saveNote(Note note) {
        try {
            Note noteSaved = this.noteRepository.save(note);
            return noteSaved;
        } catch(SaveException exception) {
            throw new SaveException();
        }
    }

    private void deleteNote(int noteId) {
        try {
            this.noteRepository.deleteById(noteId);
        } catch(DeleteException exception) {
            throw new DeleteException();
        }
    }

    private AccountNote getAccountNote(int accountId, int noteId) {
        return this.accountNoteRepository.findById(new AccountNoteId(accountId, noteId))
                .orElseThrow(() -> new NotFoundException());
    }

    private List<AccountNote> getAccountNoteByNoteId(int noteId) {
        try {
            return accountNoteRepository.findByNote_Id(noteId);
        } catch(NotFoundException exception) {
            throw new NotFoundException();
        }
    }

    private AccountNote setAccountNote(int accountId, int noteId, Right right) {
        try {
            Account account = this.accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException());
            Note note = this.noteRepository.findById(noteId).orElseThrow(() -> new NotFoundException());
            return new AccountNote(account, note, right);
        } catch(AssociationException e) {
            throw new AssociationException();
        }
    }

    /*private AccountNote setAccountNote(Account account, Note note) {
        try {
            return new AccountNote(account, note);
        } catch(AssociationException e) {
            throw new AssociationException();
        }
    }*/

    private AccountNote saveAccountNote(AccountNote accountNote) {
        try {
            return this.accountNoteRepository.save(accountNote);
        } catch(SaveException exception) {
            throw new SaveException();
        }
    }

    private void deleteAccountNote(AccountNoteId accountNoteId) {
        try {
            this.accountNoteRepository.deleteById(accountNoteId);
        } catch(DeleteException exception) {
            throw new DeleteException();
        }
    }

    /*private void removeAllAccountNoteAssociations(AccountNote accountNote) {
        try {
            accountNote.getAccount().getAccountNotes().remove(accountNote);
            accountNote.getNote().getAccountNotes().remove(accountNote);
        } catch(AssociationException exception) {
            throw new AssociationException();
        }
    }*/

    private boolean checkUserHasRight(Right right, int noteId) {
        AccountNote accountNote = this.getAccountNote(getSessionUserId(), noteId);
        return accountNote.getRight() != null && accountNote.getRight().getValue() >= right.getValue();
    }

    private boolean checkAssociationExist(int accountId, int noteId) {
        return this.accountNoteRepository.findOptionalByAccount_IdAndNote_Id(accountId, noteId).isPresent();
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