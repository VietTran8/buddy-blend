package vn.edu.tdtu.service.keycloak.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.tdtu.config.keycloak.feignclient.KeycloakFeignConfig;
import vn.edu.tdtu.dto.keycloak.KeycloakAuthTokenRequest;
import vn.edu.tdtu.dto.keycloak.KeycloakRefreshTokenRequest;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;

@FeignClient(name = "keycloak", url = "${keycloak.auth-server-url}/realms/${keycloak.realm}/", configuration = KeycloakFeignConfig.class)
public interface KeycloakClientService {

    @PostMapping(value = "/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KeycloakTokenResponse login(@RequestBody KeycloakAuthTokenRequest request);

    @PostMapping(value = "/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KeycloakTokenResponse refreshToken(@RequestBody KeycloakRefreshTokenRequest request);
}
