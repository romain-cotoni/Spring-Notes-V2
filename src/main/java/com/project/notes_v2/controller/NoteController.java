package com.project.notes_v2.controller;

import com.project.notes_v2.dto.AccountNoteDTO;
import com.project.notes_v2.exception.FailedRequestException;
import com.project.notes_v2.model.Note;
import com.project.notes_v2.dto.NoteDTO;
import com.project.notes_v2.service.NoteService;
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
@RequestMapping("api/notes")
public class NoteController {
    private final NoteService noteService;
    NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<Note> getNotes() {
        try {
            return this.noteService.getNotes();
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @GetMapping ("/filter")
    public ResponseEntity<List<Note>> getNotesByFilter(@RequestParam(name="title"  , required=false) String title,
                                                       @RequestParam(name="group"  , required=false) String group,
                                                       @RequestParam(name="content", required=false) String content,
                                                       @RequestParam(name="right"  , required=false) String right) {
        try {
            List<Note> notes = this.noteService.getNotesByFilter(title, content, right);
            return ResponseEntity.ok(notes);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNote(@PathVariable int noteId) {
        try {
            Note note = this.noteService.getNote(noteId);
            return ResponseEntity.ok(note);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> createNote(@RequestBody NoteDTO noteDTO) {
        try {
            this.noteService.createNote(noteDTO);
            return ResponseEntity.ok().body(true);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<Boolean> updateNote(@PathVariable int noteId, @RequestBody NoteDTO noteDTO) {
        try {
            this.noteService.updateNote(noteId, noteDTO);
            return ResponseEntity.ok(true);
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
    public ResponseEntity<Boolean> shareNoteWithAccount(@RequestBody AccountNoteDTO accountNoteDTO) {
        try {
            noteService.share(accountNoteDTO);
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

    @PostMapping("/unshare")
    public ResponseEntity<Boolean> unshareNoteFromAccount(@RequestBody AccountNoteDTO accountNoteDTO) {
        try {
            noteService.unshare(accountNoteDTO);
            return ResponseEntity.ok(true);
        } catch (FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

}
