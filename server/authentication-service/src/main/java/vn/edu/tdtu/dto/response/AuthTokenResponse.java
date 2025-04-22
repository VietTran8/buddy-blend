package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthTokenResponse(
        String accessToken,
        String socketAccessToken,
        Long expiresIn,
        Long refreshExpiresIn,
        String refreshToken,
        String tokenType,
        String sessionState,
        String scope
) {
    public static AuthTokenResponse from(KeycloakTokenResponse kcResponse, String socketAccessToken) {
        return new AuthTokenResponse(
                kcResponse.accessToken(),
                socketAccessToken,
                kcResponse.expiresIn(),
                kcResponse.refreshExpiresIn(),
                kcResponse.refreshToken(),
                kcResponse.tokenType(),
                kcResponse.sessionState(),
                kcResponse.scope()
        );
    }

    public static AuthTokenResponse from(KeycloakTokenResponse kcResponse) {
        return new AuthTokenResponse(
                kcResponse.accessToken(),
                null,
                kcResponse.expiresIn(),
                kcResponse.refreshExpiresIn(),
                kcResponse.refreshToken(),
                kcResponse.tokenType(),
                kcResponse.sessionState(),
                kcResponse.scope()
        );
    }
}