package vn.edu.tdtu.config.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakClientConfig {
    private final KeycloakPropsConfig keycloakPropsConfig;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .realm(keycloakPropsConfig.getRealm())
                .serverUrl(keycloakPropsConfig.getAuthServerUrl())
                .clientId(keycloakPropsConfig.getResource())
                .clientSecret(keycloakPropsConfig.getCredentials().getSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    @Bean
    public RealmResource realmResource() {
        Keycloak keycloak = keycloak();

        return keycloak.realm(keycloakPropsConfig.getRealm());
    }
}
