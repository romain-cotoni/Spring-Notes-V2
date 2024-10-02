package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Share;
import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.model.Tag;

import java.util.List;
import java.util.Set;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteDTO {
    private Integer id;

    private String title;

    private Instant created;

    private Instant modified;

    private boolean isPublic = false;

    private Share share = Share.OK;

    private String content;

    private List<Tag> tags;

    private Set<AccountNote> accountNotes;
}



