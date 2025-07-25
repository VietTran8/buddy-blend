package vn.edu.tdtu.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidation {
    public static final List<String> permitEndpoints = List.of(
            "/api/v1/auth/**",
            "/api/v1/search/**",
            "/api/v1/file/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    public Predicate<ServerHttpRequest> isSecured =
            request -> permitEndpoints.stream().noneMatch(
                    uri -> pathMatcher.match(uri, request.getURI().getPath())
            );
}