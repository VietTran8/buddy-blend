package vn.edu.tdtu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.dto.ResDTO;

@Component
@RequiredArgsConstructor
public class ForwardTokenFilter implements GatewayFilter {
    private final ObjectMapper objectMapper;
    private final RouterValidation routerValidation;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidation.isSecured.test(request)) {
            final String tokenHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (tokenHeader == null || !tokenHeader.startsWith("Bearer "))
                return onError(exchange);

            request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, tokenHeader)
                    .build();
        }

        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        ResDTO<?> errorMessage = new ResDTO<>();
        errorMessage.setMessage("You are not authenticated");
        errorMessage.setData(null);
        errorMessage.setCode(HttpStatus.UNAUTHORIZED.value());

        response.getHeaders().add("Content-Type", "application/json");

        try {
            String body = objectMapper.writeValueAsString(errorMessage);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }
}
