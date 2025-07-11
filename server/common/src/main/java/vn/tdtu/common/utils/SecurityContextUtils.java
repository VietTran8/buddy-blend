package vn.tdtu.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import vn.tdtu.common.enums.user.EUserRole;
import vn.tdtu.common.exception.UnauthorizedException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SecurityContextUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityContextUtils.class);

    public static String getUserId() {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        if (authentication.isPresent() && authentication.get().getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> claims = jwt.getClaims();
            Object userIdClaim = claims.get(Constants.JwtClaims.USER_ID);

            if (userIdClaim == null) {
                logger.warn("[getUserId] - user_id claim is null in JWT: {}", jwt);
                throw new UnauthorizedException("user_id claim is missing in the JWT");
            }

            logger.info("[getUserId] - Retrieved user_id claim: {}", userIdClaim);

            return userIdClaim.toString();
        }

        logger.warn("[getUserId] - Failed to retrieve user id claim with authentication context: {}", SecurityContextHolder.getContext().getAuthentication());

        return null;
    }

    public static void byPassAuthentication(HttpServletRequest request, RequestUtils requestUtils) {
        Optional<Authentication> existedAuthentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        String userIdHeader = request.getHeader(Constants.RequestHeader.X_FEIGN_USER_ID);

        // Check if the request is a permitted Feign call then not set to the SecurityContext
        boolean isPermittedFeignCall = userIdHeader == null;

        if(existedAuthentication.isEmpty() && !isPermittedFeignCall) {
            Authentication authentication = getAuthentication(userIdHeader);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private static Authentication getAuthentication(String userIdHeader) {
        Map<String, Object> dummyHeader = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );

        Map<String, Object> claims = Map.of(
                Constants.JwtClaims.USER_ID,
                userIdHeader
        );

        Jwt jwt = new Jwt("feign-call-token-value", null, null,
                dummyHeader,
                claims
        );

        Authentication authentication = new JwtAuthenticationToken(jwt);
        authentication.setAuthenticated(true);
        return authentication;
    }

    public static List<EUserRole> getUserRoles() {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

        if (authentication.isPresent() && authentication.get().getPrincipal() instanceof Jwt jwt) {
            Object userIdClaim = jwt.getClaims().get(Constants.JwtClaims.USER_ID);
            List<String> userRoles = jwt.getClaimAsStringList(Constants.JwtClaims.USER_ROLES);

            logger.info("[getUserRoles] - Retrieved user [{}] with roles: {}",
                    userIdClaim,
                    StringUtils.join("[", String.join(", ", userRoles), "]")
            );

            return userRoles.stream()
                    .filter(EUserRole::hasRole)
                    .map(EUserRole::valueOf)
                    .toList();
        }

        logger.warn("[getUserRoles] - Failed to retrieve user roles with authentication context: {}", SecurityContextHolder.getContext().getAuthentication());

        return Collections.emptyList();
    }
}
