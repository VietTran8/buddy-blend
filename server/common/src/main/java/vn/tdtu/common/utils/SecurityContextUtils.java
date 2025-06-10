package vn.tdtu.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import vn.tdtu.common.exception.UnauthorizedException;

public class SecurityContextUtils {
    public static String getUserId() throws UnauthorizedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            Object userIdClaim = jwt.getClaims().get("user_id");
            return userIdClaim != null ? userIdClaim.toString() : "";
        }

        return null;
    }
}
