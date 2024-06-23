package com.project.notes_v2.repository;

import com.project.notes_v2.model.Account;
import com.project.notes_v2.model.Note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer>, JpaSpecificationExecutor<Note> {

}
