package com.project.notes_v2.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDTO {
    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private String password;

    private Boolean isDevMode;

    private Boolean isToolTips;

    private Boolean isEditable;
}
