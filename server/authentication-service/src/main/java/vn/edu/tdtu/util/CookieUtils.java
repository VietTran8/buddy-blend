package vn.edu.tdtu.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import vn.edu.tdtu.constant.CommonConstant;

import java.time.Duration;

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
}
