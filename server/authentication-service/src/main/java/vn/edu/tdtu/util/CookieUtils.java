package vn.edu.tdtu.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.MessageCode;

import java.time.Duration;
import java.util.Optional;

public class CookieUtils {
    public static void setCookie(HttpServletResponse response,
                                 String cookieName,
                                 String cookieValue,
                                 Long expiresIn,
                                 String path
    ) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .path(path)
                .maxAge(Duration.ofSeconds(expiresIn))
                .secure(false)
                .sameSite(Cookie.SameSite.LAX.attributeValue())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(HttpServletResponse response,
                                    String cookieName,
                                    String path
    ) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .path(path)
                .maxAge(Duration.ZERO)
                .secure(false)
                .sameSite(Cookie.SameSite.LAX.attributeValue())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static Optional<String> getCookieValue(HttpServletRequest request,
                                          String cookieName
    ) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();

        String cookieValue = null;

        if (cookies == null)
            return Optional.empty();

        for (jakarta.servlet.http.Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                cookieValue = cookie.getValue();
                break;
            }
        }

        return Optional.ofNullable(cookieValue);
    }
}
