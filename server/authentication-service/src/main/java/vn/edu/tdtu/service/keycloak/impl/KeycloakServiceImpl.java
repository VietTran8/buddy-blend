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
import vn.edu.tdtu.dto.keycloak.KeycloakAuthTokenRequest;
import vn.edu.tdtu.dto.keycloak.KeycloakRefreshTokenOrLogoutRequest;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;
import vn.edu.tdtu.service.keycloak.interfaces.KeycloakClientService;
import vn.edu.tdtu.service.keycloak.interfaces.KeycloakService;
import vn.tdtu.common.enums.user.EUserRole;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.MessageCode;

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
    public void assignRealmRole(String keycloakUserId, List<EUserRole> role) {
        try {
            realmResource.users().get(keycloakUserId).roles()
                    .realmLevel()
                    .add(role.stream().map(
                            item -> realmResource.roles()
                                    .get(item.name())
                                    .toRepresentation()).toList()
                    );

        } catch (Exception e) {
            throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_ASSIGN_ROLES_FAILED, e.getMessage());
        }
    }

    @Override
    public void removeRealmRole(String keycloakUserId, List<EUserRole> role) {
        try {
            realmResource.users().get(keycloakUserId).roles()
                    .realmLevel()
                    .remove(role.stream().map(
                            item -> realmResource.roles().get(item.name()).toRepresentation()).toList()
                    );

        } catch (Exception e) {
            throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_REMOVE_ROLES_FAILED, e.getMessage());
        }
    }

    @Override
    public void resetPassword(String keycloakUserId, String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(password);

        try {
            realmResource.users().get(keycloakUserId)
                    .resetPassword(credentialRepresentation);
        } catch (Exception e) {
            throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_RESET_PASSWORD_FAILED, e.getMessage());
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
            throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_CREATE_USER_FAILED, e.getMessage());
        }

        int createStatus = createUserResponse.getStatus();

        if (createStatus == HttpStatus.SC_CREATED)
            return createUserResponse.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_CREATE_USER_FAILED_W_STATUS, createStatus, createUserResponse.readEntity(String.class));
    }

    @Override
    public void updateUser(String keycloakUserId, UserRepresentation userRepresentation) {
        try {
            realmResource.users()
                    .get(keycloakUserId)
                    .update(userRepresentation);
        } catch (Exception e) {
            throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_UPDATE_USER_FAILED, e.getMessage());
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
        KeycloakRefreshTokenOrLogoutRequest refreshTokenRequest = new KeycloakRefreshTokenOrLogoutRequest(
                OAuth2Constants.REFRESH_TOKEN,
                keycloakPropsConfig.getResource(),
                keycloakPropsConfig.getCredentials().getSecret(),
                refreshToken
        );

        try {
            return keycloakClientService.refreshToken(refreshTokenRequest);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            throw new UnauthorizedException(MessageCode.Authentication.AUTH_INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public boolean logoutUser(String refreshToken) {
        KeycloakRefreshTokenOrLogoutRequest logoutRequest = new KeycloakRefreshTokenOrLogoutRequest(
                keycloakPropsConfig.getResource(),
                keycloakPropsConfig.getCredentials().getSecret(),
                refreshToken
        );

        try {
            keycloakClientService.logout(logoutRequest);
            return true;
        } catch (AuthenticationException e) {
            log.error("[logout] Authentication failed with error ", e);
            throw new UnauthorizedException(MessageCode.Authentication.AUTH_INVALID_REFRESH_TOKEN);
        } catch (Exception e) {
            log.warn("[logout] Exception occurred during logout", e);
            throw new BadRequestException(MessageCode.Authentication.KEYCLOAK_LOGOUT_FAILED, e.getMessage());
        }
    }
}
