package com.project.notes_v2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.notes_v2.enumeration.Right;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "accountNote")
public class AccountNote {

    @EmbeddedId
    @EqualsAndHashCode.Include
    AccountNoteId accountNoteId;

    @JsonIgnore
    @ManyToOne
    @MapsId("accountId")
    private Account account;

    @JsonIgnore
    @ManyToOne
    @MapsId("noteId")
    private Note note;

    @Enumerated(EnumType.STRING)
    @Column(name = "rights")
    private Right right = Right.READ;

    //Constructors (define explicitly without Lombok)
    public AccountNote(Account account, Note note) {
        this.accountNoteId = new AccountNoteId(account.getId(), note.getId());
        this.account = account;
        this.note = note;
    }

    public AccountNote(Account account, Note note, Right right) {
        this.accountNoteId = new AccountNoteId(account.getId(), note.getId());
        this.account = account;
        this.note = note;
        this.right = right;
    }

}
