package com.movie_theater.service;

import com.movie_theater.entity.Role;

import java.util.Optional;

public interface CustomRoleService {
    Optional<Role> getRoleByName(String name);

    boolean existRoleName(String name);
}
