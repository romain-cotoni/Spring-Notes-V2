package com.project.notes_v2.controller;

import com.project.notes_v2.dto.AccountNoteDTO;
import com.project.notes_v2.dto.NoteRequestDTO;
import com.project.notes_v2.dto.NoteResponseDTO;
import com.project.notes_v2.exception.FailedRequestException;
import com.project.notes_v2.service.NoteService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("api/notes")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getNotes() {
        try {
            List<NoteResponseDTO> noteResponseDTOList = this.noteService.getNotes();
            return ResponseEntity.ok(noteResponseDTOList);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @GetMapping ("/filter")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByFilter(@RequestParam(name="title"  , required=false) String title,
                                                                  @RequestParam(name="group"  , required=false) String group,
                                                                  @RequestParam(name="content", required=false) String content,
                                                                  @RequestParam(name="right"  , required=false) String right) {
        try {
            List<NoteResponseDTO> noteResponseDTOList = this.noteService.getNotesByFilter(title, content, right);
            return ResponseEntity.ok(noteResponseDTOList);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponseDTO> getNote(@PathVariable int noteId) {
        try {
            NoteResponseDTO noteResponseDTO = this.noteService.getNoteResponseDTO(noteId);
            return ResponseEntity.ok(noteResponseDTO);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<NoteResponseDTO>> getNotesByTagId(@PathVariable int tagId) {
        try {
            List<NoteResponseDTO> noteResponseDTOList = this.noteService.getNotesByTagId(tagId);
            return ResponseEntity.ok(noteResponseDTOList);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@RequestBody NoteRequestDTO noteRequestDTO) {
        try {
            NoteResponseDTO note = this.noteService.createNote(noteRequestDTO);
            return ResponseEntity.ok().body(note);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @PatchMapping("/{noteId}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable int noteId, @RequestBody NoteRequestDTO noteRequestDTO) {
        try {
            NoteResponseDTO note = this.noteService.updateNote(noteId, noteRequestDTO);
            return ResponseEntity.ok(note);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException("");
        }
    }


    @DeleteMapping("/{noteId}")
    public ResponseEntity<Boolean> removeNote(@PathVariable int noteId) {
        try {
            this.noteService.eraseNote(noteId);
            return ResponseEntity.ok(true);
        } catch (FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @PostMapping("/share")
    public ResponseEntity<Boolean> shareNotesWithAccount(@RequestBody List<AccountNoteDTO> accountNotesDTO) {
        try {
            noteService.share(accountNotesDTO);
            return ResponseEntity.ok(true);
        } catch (FailedRequestException | MessagingException exception) {
            throw new FailedRequestException();
        }
    }


    @PostMapping("/unshare")
    public ResponseEntity<Boolean> unshareNoteFromAccount(@RequestBody List<AccountNoteDTO> accountNotesDTO) {
        try {
            noteService.unshare(accountNotesDTO);
            return ResponseEntity.ok(true);
        } catch (FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @PatchMapping("/updateShare")
    public ResponseEntity<Boolean> updateAccountNoteAssociation(@RequestBody AccountNoteDTO accountNoteDTO) {
        try {
            noteService.updateShare(accountNoteDTO);
            return ResponseEntity.ok(true);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

}
