package vn.edu.tdtu.config.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakPropsConfig {
    private String resource;
    private String realm;
    private String authServerUrl;
    private Credentials credentials;

    @Getter
    @Setter
    public static class Credentials {
        private String secret;
    }
}
