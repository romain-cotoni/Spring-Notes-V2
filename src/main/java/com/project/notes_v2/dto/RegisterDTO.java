package com.project.notes_v2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private String password;
}
