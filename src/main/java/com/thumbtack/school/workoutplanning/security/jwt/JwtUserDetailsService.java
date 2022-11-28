package com.thumbtack.school.workoutplanning.security.jwt;

import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.repository.UserRepository;
import com.thumbtack.school.workoutplanning.security.jwt.JwtUser;
import com.thumbtack.school.workoutplanning.security.jwt.JwtUserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user);
        log.debug("IN loadUserByUsername - user with username: '{}' successfully loaded", username);
        return jwtUser;
    }
}
