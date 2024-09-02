package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${kafka.topic.sync-user.name}")
    private String synUserTopicName;
    @Value("${kafka.topic.friend-request.name}")
    private String friendRequestTopicName;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://fcm.googleapis.com")
                .build();
    }

    @Bean
    public NewTopic friendRequestTopic(){
        return new NewTopic(friendRequestTopicName, 2, (short) 1);
    }

    @Bean
    public NewTopic syncUser(){
        return new NewTopic(synUserTopicName, 2, (short) 1);
    }
}