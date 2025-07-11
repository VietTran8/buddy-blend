package vn.tdtu.common.config;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiHandlerConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
