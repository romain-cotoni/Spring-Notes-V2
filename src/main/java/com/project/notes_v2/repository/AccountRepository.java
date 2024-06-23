package com.project.notes_v2.repository;

import com.project.notes_v2.model.Account;

import com.project.notes_v2.model.AccountNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>, JpaSpecificationExecutor<Account> {
    Account findByUsernameAndPassword(String username, String password);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);
}
