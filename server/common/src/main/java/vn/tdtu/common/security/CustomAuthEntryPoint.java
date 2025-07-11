package vn.tdtu.common.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.tdtu.common.utils.JsonUtil;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    private final JsonUtil<ResponseVM<?>> jsonParser;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader("Origin"));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");

        PrintWriter printWriter = response.getWriter();

        printWriter.write(jsonParser.stringify(new ResponseVM<>(
                MessageCode.Authentication.AUTH_UNAUTHORIZED,
                null,
                HttpServletResponse.SC_UNAUTHORIZED
        )));

        printWriter.flush();
    }
}
