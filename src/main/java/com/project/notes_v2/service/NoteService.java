package com.project.notes_v2.service;

import com.project.notes_v2.dto.NoteRequestDTO;
import com.project.notes_v2.dto.NoteResponseDTO;
import com.project.notes_v2.enumeration.Right;
import com.project.notes_v2.exception.AssociationException;
import com.project.notes_v2.exception.NotFoundException;
import com.project.notes_v2.exception.DeleteException;
import com.project.notes_v2.exception.SaveException;
import com.project.notes_v2.exception.ShareException;
import com.project.notes_v2.exception.UnauthenticatedException;
import com.project.notes_v2.exception.UnauthorizedException;

import com.project.notes_v2.mapper.NoteMapper;
import com.project.notes_v2.model.Note;
import com.project.notes_v2.model.Account;
import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.model.AccountNoteId;
import com.project.notes_v2.dto.AccountNoteDTO;

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

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final AccountRepository accountRepository;
    private final AccountNoteRepository accountNoteRepository;
    private final HttpServletRequest httpServletRequest;
    private final NoteMapper noteMapper;

    /*---------------PUBLIC METHODS---------------*/

    public List<NoteResponseDTO> getNotes() {
        return accountNoteRepository.findByAccount_Id(getSessionUserId())
                                    .stream()
                                    .map(accountNote -> noteMapper.toNoteResponseDTO(accountNote.getNote()))
                                    .toList();
    }


    public List<NoteResponseDTO> getNotesByFilter(String title, String content, String right) {
        List<Note> notes = noteRepository.findAll(NoteSpecification.filterBy(getSessionUserId(),title,content,right));
        return noteMapper.toNoteResponseDTOList(notes);
    }


    public NoteResponseDTO getNoteResponseDTO(int noteId) {
        Note note = this.getNote(noteId);
        return this.noteMapper.toNoteResponseDTO(note);
    }


    public Note getNote(int noteId) {
        AccountNote accountNote = accountNoteRepository.findById(new AccountNoteId(getSessionUserId(), noteId))
                                                       .orElseThrow(NotFoundException::new);
        return accountNote.getNote();
    }


    @Transactional
    public NoteResponseDTO createNote(NoteRequestDTO noteRequestDTO) {

        // map noteToCreate with noteDTO
        Note noteToCreate = new Note();

        // set title if empty
        noteToCreate.setTitle(StringUtils.hasText(noteRequestDTO.getTitle())  ? noteRequestDTO.getTitle() : "New note");

        if(StringUtils.hasText(noteRequestDTO.getContent())) {
            noteToCreate.setContent(noteRequestDTO.getContent());
        }

        // set datetime
        noteToCreate.setCreated(Instant.now());

        // save Note in db
        Note noteSaved = this.saveNote(noteToCreate);

        // set AccountNote association
        AccountNote accountNote = this.setAccountNote(getSessionUserId(), noteSaved.getId(), Right.OWNER);

        // save AccountNote association
        this.saveAccountNote(accountNote);

        return this.noteMapper.toNoteResponseDTO(noteSaved);
    }


    @Transactional
    public NoteResponseDTO updateNote(int noteId, NoteRequestDTO noteRequestDTO) {
        // check user is allowed to update note
        if(!this.checkUserHasRight(Right.WRITE, noteId)) { throw new UnauthorizedException(); }

        // get note to update
        Note noteToUpdate = this.getNote(noteId);

        // update note
        if(StringUtils.hasLength(noteRequestDTO.getTitle())) {
            noteToUpdate.setTitle(noteRequestDTO.getTitle());
        }
        if(StringUtils.hasLength(noteRequestDTO.getContent())) { // noteDTO.getContent() != null && !noteDTO.getContent().isEmpty()
            noteToUpdate.setContent(noteRequestDTO.getContent());
        }
        if(noteRequestDTO.getShare() != null) {
            noteToUpdate.setShare(noteRequestDTO.getShare());
        }

        // set the datetime of modification
        noteToUpdate.setModified(Instant.now());

        // save note
        Note noteSaved = this.saveNote(noteToUpdate);

        return this.noteMapper.toNoteResponseDTO(noteSaved);
    }


    @Transactional
    public void eraseNote(int noteId) {
        // check user is allowed to delete note
        if(this.checkUserHasRight(Right.DELETE, noteId)) {
            // get all AccountNote association with NOTE been deleted
            List<AccountNote> accountNotesToDeleteList = this.getAccountNoteByNoteId(noteId);

            //remove from all accounts associated
            accountNotesToDeleteList.forEach( accountNote -> {
                this.deleteAccountNote(new AccountNoteId(accountNote.getAccount().getId(), noteId));
            });

            //delete Note
            this.deleteNote(noteId);

        } else if(this.checkUserHasRight(Right.READ, noteId)) {
            //User not allowed to delete then simply unshare note instead
            int userSessionId = getSessionUserId();
            this.deleteAccountNote(new AccountNoteId(userSessionId, noteId));
        } else {
            throw new UnauthorizedException();
        }
    }


    @Transactional
    public void share(List<AccountNoteDTO> accountNotesDTO) {
        for(AccountNoteDTO accountNoteDTO: accountNotesDTO) {
            // Check current user Account has rights to share Note
            if (!this.checkUserHasRight(Right.SHARE, accountNoteDTO.getNoteId())) {
                throw new UnauthorizedException();
            }

            if(this.checkAssociationExist(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId())) {
                // If AccountNote association already exist then update RIGHT
                AccountNote accountNoteToUpdate = this.getAccountNote(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId());
                if(accountNoteDTO.getRight() != null) {
                    accountNoteToUpdate.setRight(accountNoteDTO.getRight());
                }
                // Update association Account and Note
                this.saveAccountNote(accountNoteToUpdate);
            } else { // If AccountNote doesn't exist then create
                // Map AccountNote to share
                AccountNote accountNoteToShare = this.setAccountNote(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId(), accountNoteDTO.getRight());
                // Associate Account and Note
                this.saveAccountNote(accountNoteToShare);
            }
        }
    }


    @Transactional
    public void unshare(List<AccountNoteDTO> accountNotesDTO) {
        for(AccountNoteDTO accountNoteDTO : accountNotesDTO) {
            // Check current user Account has rights to share Note
            if (this.checkUserHasRight(Right.SHARE, accountNoteDTO.getNoteId())) {
                this.unshareNote(accountNoteDTO);
            } else {
                throw new UnauthorizedException();
            }
        }
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


    /*---------------PRIVATE METHODS---------------*/

    private Note saveNote(Note note) {
        try {
            return this.noteRepository.save(note);
        } catch(SaveException exception) {
            throw new SaveException();
        }
    }

    private void unshareNote(AccountNoteDTO accountNoteDTO) {
        // Check AccountNote association exists
        if (!this.checkAssociationExist(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId())) {
            throw new ShareException();
        }

        // delete AccountNote association
        this.deleteAccountNote(new AccountNoteId(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId()));
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
                                         .orElseThrow(NotFoundException::new);
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
            Account account = this.accountRepository.findById(accountId).orElseThrow(NotFoundException::new);
            Note note       = this.noteRepository.findById(noteId).orElseThrow(NotFoundException::new);
            return new AccountNote(account, note, right);
        } catch(AssociationException e) {
            throw new AssociationException();
        }
    }


    private void saveAccountNote(AccountNote accountNote) {
        try {
            this.accountNoteRepository.save(accountNote);
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