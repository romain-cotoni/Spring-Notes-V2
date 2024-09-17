package com.project.notes_v2.dto;

import com.project.notes_v2.enumeration.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponseDTO {
    private Integer id;

    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private Role role;

    private Boolean isDevMode;

    private Boolean isToolTips;

    private Boolean isEditable;
}
