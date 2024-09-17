package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Share;
import com.project.notes_v2.model.AccountNote;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class NoteResponseDTO {
    private Integer id;

    private String title;

    private Instant created;

    private Instant modified;

    private Share share;

    private String content;

    private Set<AccountNote> accountNotes;
}
