package com.thumbtack.school.workoutplanning.config;

import com.thumbtack.school.workoutplanning.security.jwt.JwtConfigurer;
import com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider;
import com.thumbtack.school.workoutplanning.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.thumbtack.school.workoutplanning.security.oauth2.OAuth2AuthenticationSuccessHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/webjars/**", "/swagger-resources/**",
                        "/configuration/security", "/configuration/ui", "/swagger-ui.html").permitAll()
                .antMatchers("/api/auth/login", "/api/client/registration").permitAll()
                .and()
                .cors()
                .configurationSource(this.corsConfigurationSource())
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                .and()
                .oauth2Login()
                    .authorizationEndpoint()
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                        .and()
                .successHandler(this.oAuth2AuthenticationSuccessHandler());
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

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return (cors) -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedHeaders(List.of("Authorization",
                    "Cache-Control",
                    "Content-Type",
                    "Access-Control-Allow-Origin",
                    "Access-Control-Allow-Credentials"));
            corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.setExposedHeaders(List.of("Authorization"));
            return corsConfiguration;
        };
    }
}
