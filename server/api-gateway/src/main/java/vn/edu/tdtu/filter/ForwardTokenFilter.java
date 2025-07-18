package vn.edu.tdtu.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.utils.RouterValidation;
import vn.edu.tdtu.viewmodel.ResponseVM;

@Component
@RequiredArgsConstructor
@Slf4j
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
        log.warn("[ForwardTokenFilter] - Unauthorized request (missing token, or invalid token format): {}", exchange.getRequest().getURI());

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        ResponseVM<?> errorMessage = new ResponseVM<>();
        errorMessage.setMessage("Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.");
        errorMessage.setData(null);
        errorMessage.setCode(HttpStatus.UNAUTHORIZED.value());

        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            String body = objectMapper.writeValueAsString(errorMessage);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }
}
