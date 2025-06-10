package vn.edu.tdtu.service.keycloak.interfaces;

import org.keycloak.representations.idm.UserRepresentation;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;
import vn.edu.tdtu.enums.EUserRole;

import java.util.List;
import java.util.Optional;

public interface KeycloakService {
    void assignRealmRole(String keycloakUserId, List<EUserRole> role);

    void removeRealmRole(String keycloakUserId, List<EUserRole> role);

    void resetPassword(String keycloakUserId, String password);

    String createUser(UserRepresentation userRepresentation);

    void updateUser(String keycloakUserId, UserRepresentation userRepresentation);

    Optional<UserRepresentation> findUserByEmail(String email);

    KeycloakTokenResponse loginUser(String email, String password);

    boolean passwordChecking(String email, String password);

    KeycloakTokenResponse refreshToken(String refreshToken);
}
