package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.dto.request.account.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.exception.InternalErrorCode;
import com.thumbtack.school.workoutplanning.exception.InternalException;
import com.thumbtack.school.workoutplanning.mappers.dto.UserMapper;
import com.thumbtack.school.workoutplanning.model.AccountState;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.RecoveryCode;
import com.thumbtack.school.workoutplanning.model.Role;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.repository.RecoveryCodeRepository;
import com.thumbtack.school.workoutplanning.repository.RoleRepository;
import com.thumbtack.school.workoutplanning.repository.UserRepository;
import com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider;
import com.thumbtack.school.workoutplanning.utils.AuthUtils;
import com.thumbtack.school.workoutplanning.utils.EmailUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;
import static java.time.LocalDateTime.now;

@Service
@Transactional
@Slf4j
public class UserService {
    private static final int MAX_CODE = 9999;
    private static final int MIN_CODE = 1000;
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
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    @Value("${time-to-live-code-restore}")
    private String timeToLiveCode;

    public void register(User user, AuthType role) throws BadRequestException {
        Role roleUser = roleRepository.findByName(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleUser);
        user.setState(AccountState.ACTIVE);

        try {
            userRepository.save(user);
            log.info("IN register - user: {} successfully registered", user);
        } catch (DataAccessException exception) {
            log.error("Duplicate users. {}", exception.getCause().getMessage());
            throw new BadRequestException(BadRequestErrorCode.USERNAME_ALREADY_USED);
        }
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmailOrUsername(email);
        throwNotNullOrBlocked(email, user);

        Random random = new Random();
        Integer code = random.nextInt(MAX_CODE-MIN_CODE) + MIN_CODE;
        emailUtils.sendEmail(user.getEmail(), "Restore Access",
                String.format("To restore access to your account in Workout Planning application," +
                        " enter the confirmation code: %s", code));

        Supplier<LocalDateTime> timeProvider = LocalDateTime::now;

        RecoveryCode recoveryCode = new RecoveryCode(code, user, timeProvider);
        recoveryCodeRepository.save(recoveryCode);

        user.setState(AccountState.RESTORE);
        userRepository.save(user);
    }

    public void acceptCode(Integer code, String password) {
        RecoveryCode recoveryCode = recoveryCodeRepository.findByCode(code.toString());

        if (recoveryCode == null) {
            throw new BadRequestException(BadRequestErrorCode.CODE_NOT_FOUND);
        }

        if (recoveryCode.getTimeCreate().plusMinutes(Long.parseLong(timeToLiveCode)).isBefore(now())) {
            throw new BadRequestException(BadRequestErrorCode.CODE_EXPIRED);
        }

        User user = recoveryCode.getUser();
        if (!user.getState().equals(AccountState.RESTORE)) {
            throw new BadRequestException(BadRequestErrorCode.ACCOUNT_HAS_BAD_STATE);
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setState(AccountState.ACTIVE);
        userRepository.save(user);
        recoveryCodeRepository.delete(recoveryCode);
    }

    public AuthDtoResponse auth(String username, String password, HttpServletResponse response) throws BadRequestException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByEmailOrUsername(username);
            throwNotNullOrBlocked(username, user);

            setJwtToken(user, response);
            return UserMapper.INSTANCE.userToAuthDtoResponse(user);
        } catch (AuthenticationException e) {
            throw new BadRequestException(BadRequestErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }
    }

    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    public User update(String username, UpdateAccountDtoRequest request) throws AccessDeniedException {
        AuthType authType = AuthUtils.getRole();
        User user = userRepository.findByEmailOrUsername(username);
        throwNotNullOrBlocked(username, user);

        boolean isAdmin = authType == AuthType.ADMIN;
        if (!AuthUtils.getUsername().equals(user.getUsername()) && !isAdmin) {
            throw new AccessDeniedException("Action forbidden");
        }
        UserMapper.INSTANCE.updateDtoToUser(user, request);
        userRepository.save(user);
        return user;
    }

    public User setState(String username, AccountState accountState) throws BadRequestException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BadRequestException(BadRequestErrorCode.USER_NOT_FOUND);
        }

        user.setState(accountState);
        userRepository.save(user);
        return user;
    }

    public List<User> getAllByRole(String roleName) throws BadRequestException {
        try {
            Role role = roleRepository.findByName(AuthType.valueOf(roleName.toUpperCase(Locale.ROOT)));
            return userRepository.findAllByRole(role, Sort.by("username"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(BadRequestErrorCode.ROLE_NOT_FOUND);
        }
    }

    public User getById(Long id) throws BadRequestException, InternalException {
        User user = userRepository.findById(id).orElseThrow(() -> new BadRequestException(BadRequestErrorCode.USER_NOT_FOUND));

        if (!AuthType.ADMIN.equals(AuthUtils.getRole()) && !id.equals(AuthUtils.getUserId())) {
            throw new AccessDeniedException(InternalErrorCode.FORBIDDEN.getErrorString());
        }
        return user;
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_TOKEN_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public User loginWithSocial(DefaultOidcUser principal, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmailOrUsername(principal.getEmail());
        if (user == null) {
            user = new User(
                    principal.getAttribute("name"),
                    "",
                    principal.getAttribute("given_name"),
                    principal.getAttribute("family_name"),
                    principal.getAttribute("email"),
                    true
            );
            try {
                register(user, AuthType.CLIENT);
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        setJwtToken(user, httpServletResponse);
        return user;
    }

    private void setJwtToken(User user, HttpServletResponse response) {
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());

        Cookie cookie = new Cookie(JWT_TOKEN_NAME, token);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private void throwNotNullOrBlocked(String username, User user) {
        if (user == null || (user.getState() != null && user.getState().equals(AccountState.BLOCKED))) {
            throw new UsernameNotFoundException("User with username or email: " + username + " not found");
        }
    }
}
