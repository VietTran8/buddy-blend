package vn.tdtu.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@Getter
public class RequestUtils {
    @Value("${security.internal.token}")
    private String internalSecretToken;

    public final Predicate<HttpServletRequest> isFeignCall = request -> {
        String feignCallHeader = request.getHeader(Constants.RequestHeader.X_FEIGN_INTERNAL_HEADER);
        return feignCallHeader != null && feignCallHeader.equals(internalSecretToken);
    };

}
