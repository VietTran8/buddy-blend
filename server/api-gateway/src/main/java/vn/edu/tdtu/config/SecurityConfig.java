package vn.edu.tdtu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import vn.edu.tdtu.security.CustomAuthEntryPoint;
import vn.edu.tdtu.utils.RouterValidation;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthEntryPoint authEntryPoint;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(config -> config
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(RouterValidation.permitEndpoints.toArray(new String[0])).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authEntryPoint)
                )
                .build();
    }
}
