package com.movie_theater.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class CustomAccount extends User implements Serializable {
    private String role;

    public CustomAccount(String username,
                         String password,
                         Collection<? extends GrantedAuthority> authorities,
                         String role) {
        super(username, password, authorities);
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
