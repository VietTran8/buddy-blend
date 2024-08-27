package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.checkerframework.checker.units.qual.N;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://fcm.googleapis.com")
                .build();
    }
    @Bean
    public NewTopic friendRequestTopic(){
        return new NewTopic("friend-request", 2, (short) 1);
    }
}