package com.kshrd.assessment.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger logger = LoggerFactory.getLogger(JwtConverter.class);
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {

        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        logger.debug("Extracted authorities for user {}: {}", 
                getPrincipleClaimName(jwt), 
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipleClaimName(jwt)
        );
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        if (jwt.getClaim("resource_access") == null) {
            return Set.of();
        }
        
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null || resourceAccess.get(resourceId) == null) {
            return Set.of();
        }
        
        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(resourceId);
        if (resource == null || resource.get("roles") == null) {
            return Set.of();
        }
        
        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");
        if (resourceRoles == null || resourceRoles.isEmpty()) {
            return Set.of();
        }

        Collection<GrantedAuthority> authorities = resourceRoles
                .stream()
                .map(role -> {
                    if (role == null || role.isEmpty()) {
                        return null;
                    }
                    String cleanRole = role;
                    if (role.startsWith("role_")) {
                        cleanRole = role.substring(5);
                    }
                    String authority = "ROLE_" + cleanRole;
                    logger.debug("Converting role '{}' to authority '{}'", role, authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .filter(authority -> authority != null)
                .collect(Collectors.toSet());
        
        logger.debug("Extracted {} authorities from resource '{}'", authorities.size(), resourceId);
        return authorities;
    }
}