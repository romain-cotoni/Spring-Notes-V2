package com.project.notes_v2.repository;

import com.project.notes_v2.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer>, JpaSpecificationExecutor<Tag> {

    Optional<List<Tag>> findTagsByNameContainsIgnoreCase(String string);

    Optional<Tag> findTagByName(String name);
}
