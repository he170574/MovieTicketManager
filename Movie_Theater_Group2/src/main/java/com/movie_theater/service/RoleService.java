package com.movie_theater.service;

import com.movie_theater.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;

public interface RoleService {
    Role getRoleByRoleName(String roleName);

}
