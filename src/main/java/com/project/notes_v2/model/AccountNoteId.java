package com.project.notes_v2.model;

import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Account Note composite key id for association
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class AccountNoteId implements Serializable {
    private static final long serialVersionUID = 6671747125763111521L;
    private Integer accountId;
    private Integer noteId;
}
