package com.movie_theater.service.impl;

import com.movie_theater.entity.Account;
import com.movie_theater.entity.Role;
import com.movie_theater.repository.AccountRepository;
import com.movie_theater.repository.RoleRepository;
import com.movie_theater.security.CustomAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountCustomServiceImpl implements UserDetailsService {
    private AccountRepository accountRepository;

    private RoleRepository roleRepository;

    @Autowired
    public AccountCustomServiceImpl(AccountRepository accountRepository, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.getByUsernameAndDeletedIsFalse(username);
        if (account == null){
            throw new UsernameNotFoundException("User not found");
        }
        Role role = account.getRole();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (role != null){
            GrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());
            grantedAuthorities.add(authority);
        }
        return new CustomAccount(
                account.getUsername(),
                account.getPassword(),
                grantedAuthorities,
                account.getRole().getRoleName()
        );
    }
}
