package com.thumbtack.school.workoutplanning.utils;

import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.security.jwt.JwtUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    public static AuthType getRole() {
        String roleName = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toArray()[0].toString();
        return AuthType.valueOf(roleName.split("_")[1]);
    }

    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static Long getUserId() {
        return ((JwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}
