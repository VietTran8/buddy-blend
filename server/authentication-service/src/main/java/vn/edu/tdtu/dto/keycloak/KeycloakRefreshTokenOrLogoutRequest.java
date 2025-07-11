package vn.edu.tdtu.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeycloakRefreshTokenOrLogoutRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String refresh_token;

    public KeycloakRefreshTokenOrLogoutRequest(String client_id, String client_secret, String refresh_token) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.refresh_token = refresh_token;
    }
}
