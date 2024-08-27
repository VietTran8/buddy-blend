package vn.edu.tdtu.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.service.JwtUtils;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {
    private final RouterValidation validator;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if(validator.isSecured.test(request)){
            if(authMissing(request)){
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = request.getHeaders().getOrEmpty("Authorization").get(0);

            if(!jwtUtils.validateJwtToken(token)){
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            request.mutate()
                    .header("Authorization", token)
                    .build();
        }

        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        ResDTO<?> errorMessage = new ResDTO<>();
        errorMessage.setMessage("You are not authenticated");
        errorMessage.setData(null);
        errorMessage.setCode(httpStatus.value());

        response.getHeaders().add("Content-Type", "application/json");

        try {
            String body = objectMapper.writeValueAsString(errorMessage);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
}
