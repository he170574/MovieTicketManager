package com.movie_theater.service;

import com.movie_theater.entity.Role;
import com.movie_theater.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomRoleServiceImpl implements CustomRoleService {

    private RoleRepository roleRepository;

    @Autowired
    public CustomRoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findOne((Specification<Role>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("roleName"), name));
    }

    @Override
    public boolean existRoleName(String name) {
        return roleRepository.exists(RoleSpecification.withName(name));
    }

    public static class RoleSpecification {
        static Specification<Role> withName(String name){
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("roleName"), name);
        }
    }
}
