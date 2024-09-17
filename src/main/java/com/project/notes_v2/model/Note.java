package com.project.notes_v2.model;

import com.project.notes_v2.enumeration.Share;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="note")
public class Note {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 1, max = 30, message = "Title must be between 1 and 30 characters")
    private String title;

    @PastOrPresent(message = "Created note date must be in the past or present.")
    private Instant created;

    @PastOrPresent(message = "Modified note date must be in the past or present.")
    private Instant modified;

    private boolean isPublic = false;

    //TODO: custom enum validator
    @Enumerated(EnumType.STRING)
    private Share share = Share.OK;

    @Column(columnDefinition = "TEXT")
    private String content; // type TEXT in postegresql

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountNote> accountNotes = new HashSet<>(); //sharedWithAccounts

}