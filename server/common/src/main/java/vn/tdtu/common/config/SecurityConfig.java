package vn.tdtu.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import vn.tdtu.common.security.FeignInternalByPassFilter;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.EndpointUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static vn.tdtu.common.enums.user.EUserRole.ROLE_ADMIN;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(name = "security.common.ignored", havingValue = "false", matchIfMissing = true)
public class SecurityConfig {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final FeignInternalByPassFilter feignInternalByPassFilter;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointUtils.getEndpoints("/admin/**")).hasRole(ROLE_ADMIN.getRoleName())
                        .requestMatchers(
                                Arrays.stream(EndpointUtils.PermittedEndpoint.values())
                                        .map(EndpointUtils.PermittedEndpoint::getEndpoints)
                                        .flatMap(Stream::of)
                                        .toArray(String[]::new)
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(feignInternalByPassFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(source -> {
            List<String> userRoles = source.getClaimAsStringList(Constants.JwtClaims.USER_ROLES);

            return userRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        });

        return converter;
    }
}