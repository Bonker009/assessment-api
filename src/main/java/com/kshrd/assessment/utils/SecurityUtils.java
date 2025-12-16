package com.kshrd.assessment.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

/**
 * Utility class for extracting user information from JWT tokens
 */
public class SecurityUtils {

    /**
     * Extracts the user ID (sub claim) from the current JWT token
     * @return UUID of the authenticated user, or null if not authenticated
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getSubject(); // This is the user ID from Keycloak
            if (sub != null) {
                try {
                    return UUID.fromString(sub);
                } catch (IllegalArgumentException e) {
                    // If sub is not a UUID, try to get it from other claims
                    // Keycloak sometimes uses different formats
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Extracts the username from the current JWT token
     * @return Username (preferred_username) of the authenticated user, or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("preferred_username");
        }
        return null;
    }

    /**
     * Gets the JWT token from the current authentication context
     * @return Jwt token, or null if not authenticated
     */
    public static Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        return null;
    }
}
