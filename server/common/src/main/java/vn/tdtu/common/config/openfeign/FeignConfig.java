package vn.tdtu.common.config.openfeign;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import vn.tdtu.common.interceptor.FeignRequestInterceptor;
import vn.tdtu.common.utils.RequestUtils;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor(RequestUtils requestUtils) {
        return new FeignRequestInterceptor(requestUtils);
    }

}
