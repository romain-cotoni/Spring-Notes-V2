package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Share;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoteRequestDTO {

    private String title;

    private String content;

    private Share share = Share.OK;

}
