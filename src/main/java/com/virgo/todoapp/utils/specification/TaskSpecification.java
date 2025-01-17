package com.virgo.todoapp.utils.specification;

import com.virgo.todoapp.entity.meta.Task;
import com.virgo.todoapp.entity.meta.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> tasksByUser(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Task> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isBlank()) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Task> getSpecification(User user, String name) {
        return Specification.where(tasksByUser(user)).and(hasName(name));
    }
}

