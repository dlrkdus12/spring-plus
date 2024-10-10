package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String nickname;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long id, String nickname, String email, UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null" + role.name());
        }
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }
}
