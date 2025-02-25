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

import com.project.notes_v2.model.Tag;
import com.project.notes_v2.repository.AccountNoteRepository;
import com.project.notes_v2.repository.AccountRepository;
import com.project.notes_v2.repository.NoteRepository;
import com.project.notes_v2.repository.NoteSpecification;

import com.project.notes_v2.repository.TagRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class NoteService {
    private final EmailService emailService;
    private final NoteRepository noteRepository;
    private final AccountRepository accountRepository;
    private final AccountNoteRepository accountNoteRepository;
    private final TagRepository tagRepository;
    private final HttpServletRequest httpServletRequest;
    private final NoteMapper noteMapper;
    private final TagService tagService;

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


    public List<NoteResponseDTO> getNotesByTagId(Integer tagId) {
        Tag tag = tagRepository.findById(tagId)
                               .orElseThrow( () -> new NotFoundException("Not found Tag by id '" + tagId + "'"));
        List<Note> notes = tag.getNotes();
        return noteMapper.toNoteResponseDTOList(notes);
    }


    public List<NoteResponseDTO> getNotesByTagName(String tagName) {
        Tag tag = tagRepository.findTagByName(tagName)
                               .orElseThrow( () -> new NotFoundException("Not found Tag by tag name '" + tagName + "'"));
        List<Note> notes = tag.getNotes();
        return noteMapper.toNoteResponseDTOList(notes);
    }


    @Transactional
    public NoteResponseDTO createNote(NoteRequestDTO noteRequestDTO) {

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

        // Set fields of note to update
        if(StringUtils.hasLength(noteRequestDTO.getTitle())) {
            noteToUpdate.setTitle(noteRequestDTO.getTitle());
        }
        if(StringUtils.hasLength(noteRequestDTO.getContent())) {
            noteToUpdate.setContent(noteRequestDTO.getContent());
        }
        if(noteRequestDTO.getShare() != null) {
            noteToUpdate.setShare(noteRequestDTO.getShare());
        }

        if(noteRequestDTO.getTags() != null) {
            List<Tag> currentTags = new ArrayList<>(noteToUpdate.getTags());

            // Remove old tags
            for (Tag tag : currentTags) {
                noteToUpdate.removeTag(tag);
            }

            // Add new tags
            for (Tag newTag : noteRequestDTO.getTags()) {
                Tag managedTag = tagRepository.findTagByName(newTag.getName())
                                              .orElseGet(() -> tagService.createTag(newTag.getName()));
                noteToUpdate.addTag(managedTag);
            }
        }

        // Set the datetime of modification
        noteToUpdate.setModified(Instant.now());

        // Save note
        Note noteSaved = this.saveNote(noteToUpdate);

        // Clean up orphaned tags
        tagService.deleteOrphanTags();

        return this.noteMapper.toNoteResponseDTO(noteSaved);
    }


    @Transactional
    public void eraseNote(int noteId) {
        // Check user is allowed to delete note
        if(this.checkUserHasRight(Right.DELETE, noteId)) {
            // Get all AccountNote association with NOTE been deleted
            List<AccountNote> accountNotesToDeleteList = this.getAccountNoteByNoteId(noteId);

            // Remove from all accounts associated
            accountNotesToDeleteList.forEach( accountNote -> {
                this.deleteAccountNote(new AccountNoteId(accountNote.getAccount().getId(), noteId));
            });

            // Delete Note
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
    public void share(List<AccountNoteDTO> accountNotesDTO) throws MessagingException {
        // Get username of sharing user for notification
        String usernameSharing = "";
        Optional<Account> accountSession = this.accountRepository.findById(this.getSessionUserId());
        if(accountSession.isPresent()) {
            usernameSharing = accountSession.get().getUsername();
        }

        for(AccountNoteDTO accountNoteDTO: accountNotesDTO) {
            // Check current user Account has rights to share Note
            if (!this.checkUserHasRight(Right.SHARE, accountNoteDTO.getNoteId())) {
                throw new UnauthorizedException();
            }

            String emailUserShared;
            String usernameShared;
            String titleNoteShared;

            if(this.checkAssociationExist(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId())) {
                // If AccountNote association already exist then update RIGHT
                AccountNote accountNoteToUpdate = this.getAccountNote(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId());
                if(accountNoteDTO.getRight() != null) {
                    accountNoteToUpdate.setRight(accountNoteDTO.getRight());
                }
                // Get email of user shared for notification
                emailUserShared = accountNoteToUpdate.getAccount().getEmail();
                // Get username of shared user for notification
                usernameShared = accountNoteToUpdate.getAccount().getUsername();
                // Get title of note shared for notification
                titleNoteShared = accountNoteToUpdate.getNote().getTitle();
                // Update association Account and Note
                this.saveAccountNote(accountNoteToUpdate);
            } else { // If AccountNote doesn't exist then create
                // Map AccountNote to share
                AccountNote accountNoteToShare = this.setAccountNote(accountNoteDTO.getAccountId(), accountNoteDTO.getNoteId(), accountNoteDTO.getRight());
                // Get email for notification
                emailUserShared = accountNoteToShare.getAccount().getEmail();
                // Get username of shared user for notification
                usernameShared = accountNoteToShare.getAccount().getUsername();
                // Get title of note shared for notification
                titleNoteShared = accountNoteToShare.getNote().getTitle();
                // Associate Account and Note
                this.saveAccountNote(accountNoteToShare);
            }
            this.emailService.sendShareNoteNotification(emailUserShared, usernameSharing, usernameShared, titleNoteShared);
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