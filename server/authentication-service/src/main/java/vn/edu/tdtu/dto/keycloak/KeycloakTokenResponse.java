package vn.edu.tdtu.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_in")
        Long expiresIn,
        @JsonProperty("refresh_expires_in")
        Long refreshExpiresIn,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("session_state")
        String sessionState,
        @JsonProperty("scope")
        String scope
) {
}
