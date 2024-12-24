package vn.edu.tdtu.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {
    public static String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
