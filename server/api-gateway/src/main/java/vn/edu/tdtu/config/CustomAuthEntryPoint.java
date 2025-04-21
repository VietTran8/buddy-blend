package vn.edu.tdtu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.utils.JsonUtil;

@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private final JsonUtil<ResDTO<?>> jsonParser;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, org.springframework.security.core.AuthenticationException ex) {
        var response = exchange.getResponse();
        var request = exchange.getRequest();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String origin = request.getHeaders().getOrigin();

        if (origin != null) {
            response.getHeaders().set("Access-Control-Allow-Origin", origin);
            response.getHeaders().set("Access-Control-Allow-Credentials", "true");
            response.getHeaders().set("Access-Control-Allow-Headers", "*");
            response.getHeaders().set("Access-Control-Allow-Methods", "*");
        }

        String body = jsonParser.stringify(new ResDTO<>(
                HttpStatus.UNAUTHORIZED.value(),
                "You are not authenticated [From gateway]",
                null
        ));

        byte[] bytes = body.getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
