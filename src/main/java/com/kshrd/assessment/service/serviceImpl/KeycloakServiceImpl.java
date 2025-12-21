package com.kshrd.assessment.service.serviceImpl;

import com.kshrd.assessment.dto.auth.LoginRequest;
import com.kshrd.assessment.dto.auth.LoginResponse;
import com.kshrd.assessment.dto.auth.UserRequest;
import com.kshrd.assessment.dto.teacher.TeacherResponse;
import com.kshrd.assessment.service.IKeycloakService;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.NonNull;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Service
public class KeycloakServiceImpl implements IKeycloakService {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakServiceImpl.class);
    private final Keycloak keycloak;
    private final String realm;
    private final String authServerUrl;
    private final String clientId;
    private final String clientSecret;

    public KeycloakServiceImpl(
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret
    ) {
        this.realm = realm;
        this.authServerUrl = authServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        if (clientSecret != null && !clientSecret.isBlank()) {

            this.keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();
            logger.info("Keycloak admin client initialized successfully");
        } else {
            throw new IllegalStateException(
                    "keycloak.client-secret must be configured for admin operations"
            );
        }
    }

    public String createUser(UserRequest userRequest) {
        RealmResource realmResource = keycloak.realm(realm);
        UserRepresentation user = getUserRepresentation(userRequest);
        try (Response response = realmResource.users().create(user)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                String error = response.hasEntity()
                        ? response.readEntity(String.class)
                        : "Unknown error";

                throw new IllegalStateException(
                        "Failed to create user. Status=" + response.getStatus() + ", Error=" + error
                );
            }
            String userId = CreatedResponseUtil.getCreatedId(response);
            assignTeacherRole(userId);
            return userId;
        }
    }

    private void assignTeacherRole(String userId) {
        RealmResource realmResource = keycloak.realm(realm);
        ClientRepresentation client = realmResource.clients()
                .findByClientId(clientId)
                .stream()
                .findFirst()
                .orElseThrow();
        ClientResource clientResource = realmResource.clients().get(client.getId());
        RoleRepresentation teacherRole = clientResource.roles().get("role_teacher").toRepresentation();
        realmResource.users().get(userId).roles().clientLevel(client.getId()).add(List.of(teacherRole));

    }


    private static @NonNull UserRepresentation getUserRepresentation(UserRequest userRequest) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequest.username());
        user.setEmail(userRequest.email());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRequest.password());
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));
        return user;
    }

    public String getUser(String userId) {
        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(userId);
        return userResource.toString();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            logger.debug("Attempting login for user: {}", loginRequest.username());

            Keycloak userKeycloak;
            try {
                userKeycloak = KeycloakBuilder.builder()
                        .serverUrl(authServerUrl)
                        .realm(realm)
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .username(loginRequest.username())
                        .password(loginRequest.password())
                        .grantType(OAuth2Constants.PASSWORD)
                        .build();

                userKeycloak.tokenManager().getAccessToken();
            } catch (jakarta.ws.rs.BadRequestException e) {
                logger.warn("Client {} doesn't support password grant, trying with admin-cli", clientId);
                userKeycloak = KeycloakBuilder.builder()
                        .serverUrl(authServerUrl)
                        .realm(realm)
                        .clientId(clientId)
                        .clientSecret(clientSecret)// Public client that supports password grant
                        .username(loginRequest.username())
                        .password(loginRequest.password())
                        .grantType(OAuth2Constants.PASSWORD)
                        .build();
            }

            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();

            logger.debug("Login successful for user: {}", loginRequest.username());

            return new LoginResponse(
                    tokenResponse.getToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getTokenType(),
                    tokenResponse.getExpiresIn(),
                    tokenResponse.getRefreshExpiresIn()
            );
        } catch (jakarta.ws.rs.BadRequestException e) {
            logger.error("Bad request during login: {}", e.getMessage());
            throw new RuntimeException("Login failed: The client doesn't support password grant. Please enable 'Direct Access Grants' for client '" + clientId + "' in Keycloak.", e);
        } catch (jakarta.ws.rs.NotAuthorizedException e) {
            logger.error("Unauthorized during login: {}", e.getMessage());
            throw new RuntimeException("Login failed: Invalid username or password.", e);
        } catch (jakarta.ws.rs.NotFoundException e) {
            logger.error("Not found during login: {}", e.getMessage());
            throw new RuntimeException("Login failed: Realm or client not found. Please verify Keycloak configuration.", e);
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            throw new RuntimeException("Login failed: " + e.getMessage() + ". Please check Keycloak configuration and ensure 'Direct Access Grants' is enabled for the client.", e);
        }
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    @Override
    public List<TeacherResponse> getAllTeachers() {

        ClientResource clientResource =
                keycloak.realm(realm).clients().get(clientId);

        List<UserRepresentation> users =
                clientResource.roles()
                        .get("role_teacher")
                        .getUserMembers();

        return users.stream()
                .map(user -> new TeacherResponse(
                        user.getId(),
                        safe(user.getUsername()),
                        safe(user.getEmail())
                ))
                .toList();
    }
}