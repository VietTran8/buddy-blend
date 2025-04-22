package vn.tdtu.edu.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import vn.tdtu.edu.exception.UnauthorizedException;

public class SecurityContextUtils {
    public static String getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            Object userIdClaim = jwt.getClaims().get("user_id");
            return userIdClaim != null ? userIdClaim.toString() : "";
        }

        throw new UnauthorizedException("You are not authenticated");
    }
}
