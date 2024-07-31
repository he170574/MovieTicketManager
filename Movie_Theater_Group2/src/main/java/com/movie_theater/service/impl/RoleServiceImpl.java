package com.movie_theater.service.impl;

import com.movie_theater.entity.Role;
import com.movie_theater.repository.RoleRepository;
import com.movie_theater.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleByRoleName(String roleName) {
        return roleRepository.getByRoleName(roleName);
    }
}
