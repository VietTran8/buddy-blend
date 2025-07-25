package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import vn.tdtu.common.utils.JwtUtils;

@Configuration
public class AppConfig {
    @Bean
    public NewTopic chattingTopic() {
        return new NewTopic("chatting", 2, (short) 1);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

}
