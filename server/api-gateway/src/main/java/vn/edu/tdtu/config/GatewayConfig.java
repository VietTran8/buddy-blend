package vn.edu.tdtu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.edu.tdtu.filter.ForwardTokenFilter;

@Configuration
@EnableHystrix
@RequiredArgsConstructor
public class GatewayConfig {
    private final ForwardTokenFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("auth-service", r -> r.path("/api/v1/auth/**", "/api/v1/admin/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .route("search-service", r -> r.path("/api/v1/search/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://search-service"))
                .route("message-service", r -> r.path("/api/v1/messages/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://message-service"))
                .route("post-service", r -> r.path("/api/v1/posts/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://post-service"))
                .route("interaction-service", r -> r.path("/api/v1/interactions/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://interaction-service"))
                .route("file-service", r -> r.path("/api/v1/file/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://file-service"))
                .route("notification-service", r -> r.path("/api/v1/notifications/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://notification-service"))
                .route("group-service", r -> r.path("/api/v1/groups/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://group-service"))
                .route("story-service", r -> r.path("/api/v1/stories/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://story-service"))
                .build();
    }
}
