package com.project.notes_v2.repository;

import com.project.notes_v2.enumeration.Right;
import com.project.notes_v2.model.Note;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NoteSpecification {
    public static Specification<Note> filterBy(int accountId, String title, String content, String right) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join with AccountNote
            Join<Object, Object> accountNoteJoin = root.join("accountNotes");

            // Filter based on accountId
            predicates.add(criteriaBuilder.equal(accountNoteJoin.get("account").get("id"), accountId));


            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (content != null && !content.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content.toLowerCase() + "%"));
            }

            if (right != null && !right.isEmpty()) {
                Right rightEnum = getRightByLabel(right);
                if(rightEnum != null) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(accountNoteJoin.get("right"), rightEnum),
                            criteriaBuilder.equal(accountNoteJoin.get("account").get("id"), accountId)
                    ));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Right getRightByLabel(String label) {
        for (Right right : Right.values()) {
            if (right.getLabel().equalsIgnoreCase(label)) {
                return right;
            }
        }
        return null;
    }

}
