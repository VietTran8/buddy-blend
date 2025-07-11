package vn.edu.tdtu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.utils.JsonUtil;
import vn.edu.tdtu.viewmodel.ResponseVM;

@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint {
    private final JsonUtil<ResponseVM<?>> jsonParser;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, org.springframework.security.core.AuthenticationException ex) {
        var response = exchange.getResponse();
        var request = exchange.getRequest();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String origin = request.getHeaders().getOrigin();

        if (origin != null) {
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        }

        String body = jsonParser.stringify(new ResponseVM<>(
                "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.",
                null,
                HttpStatus.UNAUTHORIZED.value()
        ));

        byte[] bytes = body.getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
