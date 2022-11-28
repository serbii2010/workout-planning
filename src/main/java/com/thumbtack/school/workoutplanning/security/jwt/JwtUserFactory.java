package com.thumbtack.school.workoutplanning.security.jwt;

import com.thumbtack.school.workoutplanning.model.Role;
import com.thumbtack.school.workoutplanning.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public final class JwtUserFactory {
    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getRole()),
                true,
                null
        );
    }

    private static List<? extends GrantedAuthority> mapToGrantedAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName().getType()));
    }
}
