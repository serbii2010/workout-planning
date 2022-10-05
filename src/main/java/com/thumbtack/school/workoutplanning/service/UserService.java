package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.dto.request.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.auth.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.UserMapper;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.Role;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.repository.RoleRepository;
import com.thumbtack.school.workoutplanning.repository.UserRepository;
import com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User register(User user, AuthType role) throws BadRequestException {
        Role roleUser = roleRepository.findByName(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleUser);
        user.setIsActive(true);

        try {
            User registeredUser = userRepository.save(user);
            log.info("IN register - user: {} successfully registered", registeredUser);

            return registeredUser;
        } catch (DataAccessException exception) {
            log.error("Duplicate users. {}", exception.getCause().getMessage());
            throw new BadRequestException(BadRequestErrorCode.USERNAME_ALREADY_USED);
        }
    }

    public AuthDtoResponse auth(String username, String password, HttpServletResponse response) throws BadRequestException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = findByUsername(username);

            if (user == null || !user.getIsActive()) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getRole());

            Cookie cookie = new Cookie(JWT_TOKEN_NAME, token);
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);
            return UserMapper.INSTANCE.userToDtoResponse(user);
        } catch (AuthenticationException e) {
            throw new BadRequestException(BadRequestErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }
    }

    public List<User> getAll() {
        List<User> result = userRepository.findAll();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    public User findById(Long id) {
        User result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result, id);
        return result;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted", id);
    }

    public User update(String username, UpdateAccountDtoRequest request) throws AccessDeniedException {
        String roleName = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toArray()[0].toString();
        AuthType authType = AuthType.valueOf(roleName.split("_")[1]);
        User user = userRepository.findByUsername(username);
        boolean isAdmin = authType == AuthType.ADMIN;
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getUsername()) && !isAdmin) {
            throw new AccessDeniedException("Action forbidden");
        }
        UserMapper.INSTANCE.updateDtoToUser(user, request);
        userRepository.save(user);
        return user;
    }

    public User setActive(String username, boolean isActive) throws BadRequestException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BadRequestException(BadRequestErrorCode.USER_NOT_FOUND);
        }

        user.setIsActive(isActive);
        userRepository.save(user);
        return user;
    }

    public List<User> getAllByRole(String roleName) throws BadRequestException {
        if (roleName == null) {
            throw new BadRequestException(BadRequestErrorCode.ROLE_NOT_FOUND);
        }
        try {
            Role role = roleRepository.findByName(AuthType.valueOf(roleName.toUpperCase(Locale.ROOT)));
            return userRepository.findAllByRole(role);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(BadRequestErrorCode.ROLE_NOT_FOUND);
        }
    }

    public User getById(Long id) throws BadRequestException {
        if (userRepository.findById(id).isEmpty()) {
            throw new BadRequestException(BadRequestErrorCode.USER_NOT_FOUND);
        }
        return userRepository.findById(id).get();
    }
}
