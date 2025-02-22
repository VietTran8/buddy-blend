package vn.tdtu.edu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value("${kafka.topic.user-connected.name}")
    private String userConnectedTopicName;
    @Value("${kafka.topic.user-disconnected.name}")
    private String userDisconnectedTopicName;

    @Bean
    public JsonMessageConverter jsonMessageConverter(){
        return new JsonMessageConverter();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public NewTopic userConnectedTopic() {
        return new NewTopic(userConnectedTopicName, 2, (short) 1);
    }

    @Bean
    public NewTopic userDisconnectedTopic() {
        return new NewTopic(userDisconnectedTopicName, 2, (short) 1);
    }
}
