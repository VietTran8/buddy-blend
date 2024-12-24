package vn.edu.tdtu.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.util.JsonUtil;
import vn.edu.tdtu.util.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final JsonUtil<ResDTO<?>> jsonParser;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null) {
                Authentication authentication = getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            PrintWriter printWriter = response.getWriter();

            printWriter.write(jsonParser.stringify(new ResDTO<>(
                    e.getMessage(),
                    null,
                    HttpServletResponse.SC_UNAUTHORIZED
            )));
            printWriter.flush();
        }
    }

    private Authentication getAuthentication(String jwt) {
        String userId = jwtUtils.getUserIdFromJwtToken(jwt);

        if(userId.isEmpty())
            throw new UnauthorizedException("You are not authenticated");

        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
