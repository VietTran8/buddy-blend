package vn.tdtu.common.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

@UtilityClass
public class EndpointUtils {

    public static String addPrefix(String path) {
        if (path == null || path.isEmpty()) {
            return Constants.API_V1_PREFIX;
        }
        if (path.startsWith("/")) {
            return Constants.API_V1_PREFIX + path;
        }
        return Constants.API_V1_PREFIX + "/" + path;
    }

    public static String[] getEndpoints(String... paths) {
        if (paths == null || paths.length == 0) {
            return new String[] { };
        }

        return Stream.of(paths)
                .map(EndpointUtils::addPrefix)
                .toArray(String[]::new);
    }

    @Getter
    public enum PermittedEndpoint {
        AUTHENTICATION(EndpointUtils.getEndpoints("/auth/**")),
        GROUP(EndpointUtils.getEndpoints("/groups/min/**")),
        POST(EndpointUtils.getEndpoints("/posts/user/**", "/banned-word/**", "/posts/search")),
        SEARCH(EndpointUtils.getEndpoints("/search", "/search/fetch")),
        COMMON("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"),
        USER(EndpointUtils.getEndpoints("/users",
                "/users/for-auth/**",
                "/users/by-id/**",
                "/users/suggestions/group/**",
                "/users/by-ids",
                "/users/save",
                "/users/exists/**",
                "/users/disable")),
        STORY(),
        NOTIFICATION(),
        MESSAGE(),
        INTERACTION();

        PermittedEndpoint(String... endpoints) {
            this.endpoints = endpoints;
        }

        private final String[] endpoints;
    }

}
