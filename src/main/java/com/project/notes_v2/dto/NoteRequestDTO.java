package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Share;
import com.project.notes_v2.model.Tag;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoteRequestDTO {

    private String title;

    private String content;

    private List<Tag> tags;

    private Share share = Share.OK;

}
