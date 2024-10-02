package com.project.notes_v2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequestDTO {
    private String name;
    private int noteId;
}
