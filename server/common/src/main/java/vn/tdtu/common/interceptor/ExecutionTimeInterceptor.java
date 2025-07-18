package vn.tdtu.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.tdtu.common.utils.Constants;

@Component
@Slf4j
public class ExecutionTimeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(Constants.REQUEST_ATTRIBUTE_START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(Constants.REQUEST_ATTRIBUTE_START_TIME);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Done handle [{}] {} in {} ms", request.getMethod(), request.getRequestURI(), duration);
    }
}