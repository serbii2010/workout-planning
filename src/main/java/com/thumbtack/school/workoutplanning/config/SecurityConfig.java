package com.thumbtack.school.workoutplanning.config;

import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.security.jwt.JwtConfigurer;
import com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String ADMIN_ENDPOINT = "/api/admin/**";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String ADMIN_REGISTRATION_ENDPOINT = "/api/admin/registration";
    private static final String CLIENT_REGISTRATION_ENDPOINT = "/api/client/registration";

    private static final String CLIENT_SCHEDULE_ENDPOINT = "/api/schedule/**";

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers("/v2/api-docs", "/webjars/**", "/swagger-resources/**",
                        "/configuration/security", "/configuration/ui", "/swagger-ui.html").permitAll()
                .antMatchers(ADMIN_REGISTRATION_ENDPOINT).anonymous()
                .antMatchers(CLIENT_REGISTRATION_ENDPOINT).anonymous()
                .antMatchers(ADMIN_ENDPOINT).hasRole(AuthType.ADMIN.name())
                .antMatchers(CLIENT_SCHEDULE_ENDPOINT).hasRole(AuthType.CLIENT.name())
//                .antMatchers(CLIENT_SCHEDULE_ENDPOINT).permitAll()
//                .antMatchers(ADMIN_ENDPOINT).permitAll()
//                .antMatchers("/**").denyAll()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
