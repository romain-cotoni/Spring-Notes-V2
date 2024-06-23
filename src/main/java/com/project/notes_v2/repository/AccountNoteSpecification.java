package com.project.notes_v2.repository;

import com.project.notes_v2.enumeration.Right;
import com.project.notes_v2.model.AccountNote;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AccountNoteSpecification {
    public static Specification<AccountNote> filterBy(int accountId, String title, String content, String right) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("account").get("id"), accountId));

            /*if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note").get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (content != null && !content.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note").get("content")), "%" + content.toLowerCase() + "%"));
            }*/

            if (right != null && !right.isEmpty()) {
                Right rightEnum = getRightByLabel(right);
                if(rightEnum != null) {
                    predicates.add(criteriaBuilder.equal(root.get("right"), rightEnum));
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
