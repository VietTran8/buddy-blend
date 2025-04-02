package vn.edu.tdtu.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    private final JsonUtil<ResDTO<?>> jsonParser;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        PrintWriter printWriter = response.getWriter();

        printWriter.write(jsonParser.stringify(new ResDTO<>(
                "You are not authenticated",
                null,
                HttpServletResponse.SC_UNAUTHORIZED
        )));
        printWriter.flush();
    }
}
