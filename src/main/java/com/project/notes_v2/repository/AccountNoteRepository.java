package com.project.notes_v2.repository;

import com.project.notes_v2.model.AccountNote;
import com.project.notes_v2.model.AccountNoteId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountNoteRepository extends JpaRepository<AccountNote, AccountNoteId>, JpaSpecificationExecutor<AccountNote> {
    AccountNote findByAccountNoteId(AccountNoteId id);
    Optional<AccountNote> findOptionalByAccount_IdAndNote_Id(Integer accountId, Integer noteId);
    List<AccountNote> findByAccount_Id(Integer accountId);
    List<AccountNote> findByNote_Id(Integer noteId);
    List<AccountNote> findByAccount_IdAndNote_TitleContainingIgnoreCase(Integer accountId, String title);

}
