package com.kshrd.assessment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kshrd.assessment.dto.response.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtConverter jwtConverter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtConverter jwtConverter, ObjectMapper objectMapper) {
        this.jwtConverter = jwtConverter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/v4/auth/register", "/api/v4/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/inform", "/actuator/**").permitAll()
                        .anyRequest().authenticated())
                        .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
                        .exceptionHandling((exceptions) -> exceptions
                                .authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler()))
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            
            ApiResponse<Object> apiResponse = ApiResponse.error(
                    "Unauthorized: " + authException.getMessage(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "UNAUTHORIZED",
                    request.getRequestURI()
            );
            
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            
            ApiResponse<Object> apiResponse = ApiResponse.error(
                    "Access Denied: " + accessDeniedException.getMessage(),
                    HttpStatus.FORBIDDEN.value(),
                    "ACCESS_DENIED",
                    request.getRequestURI()
            );
            
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        };
    }


}
