package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Right;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountNoteDTO {
    private Integer accountId;

    private Integer noteId;

    private String username;

    private Right right;

    private String message;
}
