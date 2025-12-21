package com.kshrd.assessment.config;


import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("my-realm")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();
    }


}
