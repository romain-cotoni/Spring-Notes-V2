package com.project.notes_v2.controller;

import com.project.notes_v2.exception.FailedRequestException;
import com.project.notes_v2.model.Tag;
import com.project.notes_v2.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/tags")
public class TagController {


    private final TagService tagService;


    @GetMapping("/filter")
    public ResponseEntity<List<Tag>> getAccountsByFilter(@RequestParam(name="name", required=false) String name) {
        try {
            List<Tag> tags = this.tagService.getTagsByFilter(name);
            return ResponseEntity.ok(tags);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @GetMapping("/{noteId}")
    public ResponseEntity<List<Tag>> getTagsByNoteId(@PathVariable int noteId) {
        try {
            List<Tag> tags = tagService.getTagsByNoteId(noteId);
            return ResponseEntity.ok(tags);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody String tagName) {
        try {
            Tag tag = this.tagService.createTag(tagName);
            return ResponseEntity.ok().body(tag);
        } catch(FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }


}