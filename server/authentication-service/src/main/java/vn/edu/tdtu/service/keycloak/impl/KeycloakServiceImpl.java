package vn.edu.tdtu.service.keycloak.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.config.keycloak.KeycloakPropsConfig;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.keycloak.KeycloakAuthTokenRequest;
import vn.edu.tdtu.dto.keycloak.KeycloakRefreshTokenRequest;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.service.keycloak.interfaces.KeycloakClientService;
import vn.edu.tdtu.service.keycloak.interfaces.KeycloakService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {
    private final RealmResource realmResource;
    private final KeycloakClientService keycloakClientService;
    private final KeycloakPropsConfig keycloakPropsConfig;

    @Override
    public void assignRealmRole(String userId, List<EUserRole> role) {
        try {
            realmResource.users().get(userId).roles()
                    .realmLevel()
                    .add(role.stream().map(item -> realmResource.roles().get(item.name()).toRepresentation()).toList());
        } catch (Exception e) {
            throw new BadRequestException("Failed to assign roles" + e.getMessage());
        }
    }

    @Override
    public void resetPassword(String userId, String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(password);

        try {
            realmResource.users().get(userId)
                    .resetPassword(credentialRepresentation);
        } catch (Exception e) {
            throw new BadRequestException("Failed to reset password" + e.getMessage());
        }
    }

    @Override
    public String createUser(UserRepresentation userRepresentation) {
        Response createUserResponse;

        try {
            createUserResponse = realmResource
                    .users()
                    .create(userRepresentation);
        } catch (Exception e) {
            throw new BadRequestException("Failed to create new user" + e.getMessage());
        }

        int createStatus = createUserResponse.getStatus();

        if (createStatus == HttpStatus.SC_CREATED)
            return createUserResponse.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        throw new BadRequestException(String.format("[%s] Failed to create new user: {%s}", createStatus, createUserResponse.readEntity(String.class)));
    }

    @Override
    public void updateUser(String userId, UserRepresentation userRepresentation) {
        try {
            realmResource.users()
                    .get(userId)
                    .update(userRepresentation);
        } catch (Exception e) {
            throw new BadRequestException("Failed to update user" + e.getMessage());
        }
    }

    @Override
    public Optional<UserRepresentation> findUserByEmail(String email) {
        List<UserRepresentation> userRepresentations = realmResource.users()
                .search(email);

        return Optional.ofNullable(userRepresentations.isEmpty() ? null : userRepresentations.get(0));
    }

    @Override
    public KeycloakTokenResponse loginUser(String email, String password) {
        KeycloakAuthTokenRequest request = new KeycloakAuthTokenRequest(
                OAuth2Constants.PASSWORD,
                keycloakPropsConfig.getResource(),
                keycloakPropsConfig.getCredentials().getSecret(),
                email,
                password
        );

        return keycloakClientService.login(request);
    }

    @Override
    public boolean passwordChecking(String email, String password) {
        KeycloakAuthTokenRequest request = new KeycloakAuthTokenRequest(
                OAuth2Constants.PASSWORD,
                keycloakPropsConfig.getResource(),
                keycloakPropsConfig.getCredentials().getSecret(),
                email,
                password
        );

        try {
            keycloakClientService.login(request);
            return true;
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    @Override
    public KeycloakTokenResponse refreshToken(String refreshToken) {
        KeycloakRefreshTokenRequest request = new KeycloakRefreshTokenRequest(
                OAuth2Constants.REFRESH_TOKEN,
                keycloakPropsConfig.getResource(),
                keycloakPropsConfig.getCredentials().getSecret(),
                refreshToken
        );

        try {
            return keycloakClientService.refreshToken(request);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            throw new UnauthorizedException(MessageCode.AUTH_INVALID_REFRESH_TOKEN);
        }
    }
}
