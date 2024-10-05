package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Share;
import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.model.Tag;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class NoteResponseDTO {
    private Integer id;

    private String title;

    private Instant created;

    private Instant modified;

    private Share share;

    private String content;

    private List<Tag> tags;

    private Set<AccountNote> accountNotes;
}
