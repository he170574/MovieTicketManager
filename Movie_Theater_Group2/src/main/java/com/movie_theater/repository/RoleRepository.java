package com.movie_theater.repository;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Promotion;
import com.movie_theater.entity.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role> {
    Role getByRoleName(String roleName);
}
