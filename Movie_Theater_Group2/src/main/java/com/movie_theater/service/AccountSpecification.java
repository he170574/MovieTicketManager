package com.movie_theater.service;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

interface AccountSpecification {
    static Specification<Account> withFullNameContains(String fullName) {
        return (root, query, cb) -> cb.like(root.get("fullName"), "%" + fullName + "%");
    }

    static Specification<Account> withStatus(String status) {
        boolean deleted = Objects.equals(status, "1");
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

    static Specification<Account> withEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }

    static Specification<Account> withUsername(String username) {
        return (root, query, cb) -> cb.equal(root.get("username"), username);
    }

    static Specification<Account> withRole(Role role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }
}