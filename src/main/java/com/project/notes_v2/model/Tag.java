package com.project.notes_v2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "name") // Generate equals() and hashCode() based on the name field (id)
@Entity
@Table(name="tag")
public class Tag {

    @Id
    @Column(unique=true)
    @NotNull(message = "Tag cannot be null")
    @NotBlank(message = "Tag cannot be blank")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private List<Note> notes = new ArrayList<>();

}