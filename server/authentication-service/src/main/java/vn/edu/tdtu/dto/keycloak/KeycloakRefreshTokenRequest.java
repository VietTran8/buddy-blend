package vn.edu.tdtu.dto.keycloak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KeycloakRefreshTokenRequest extends KeycloakBaseTokenRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String refresh_token;
}
