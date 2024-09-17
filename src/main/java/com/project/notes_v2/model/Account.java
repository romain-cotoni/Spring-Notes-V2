package com.project.notes_v2.model;

import com.project.notes_v2.enumeration.Active;
import com.project.notes_v2.enumeration.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="account")
public class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Size(min = 1, max = 30, message = "Firstname must be between 1 and 30 characters")
    private String firstname;

    @Size(min = 1, max = 30, message = "Lastname must be between 1 and 30 characters")
    private String lastname;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 1, max = 30, message = "Username must be between 1 and 30 characters")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    //TODO: custom password validator
    @NotBlank(message = "Password is mandatory")
    private String password;

    //TODO:custom enum validator
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private Role role;

    @PastOrPresent(message = "Created account date must be in the past or present.")
    private Instant created;

    @PastOrPresent(message = "Modified account date must be in the past or present.")
    private Instant modified;

    private Boolean isDevMode = true;

    private Boolean isToolTips = true;

    private Boolean isEditable = true;

    @Enumerated(EnumType.STRING)
    private Active active;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountNote> accountNotes = new HashSet<>(); //sharedNotes
}
