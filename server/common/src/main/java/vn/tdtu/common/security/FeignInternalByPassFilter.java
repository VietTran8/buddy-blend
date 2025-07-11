package vn.tdtu.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.tdtu.common.utils.RequestUtils;
import vn.tdtu.common.utils.SecurityContextUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FeignInternalByPassFilter extends OncePerRequestFilter {
    private final RequestUtils requestUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if(requestUtils.isFeignCall.test(request)) {
            SecurityContextUtils.byPassAuthentication(request, requestUtils);
        }

        filterChain.doFilter(request, response);
    }
}
