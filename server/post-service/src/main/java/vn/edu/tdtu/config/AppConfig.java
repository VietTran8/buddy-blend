package vn.edu.tdtu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value("${kafka.topic.interact-noti.name}")
    private String notificationName;
    @Value("${kafka.topic.sync-post.name}")
    private String syncPostTopicName;
    @Value("${kafka.topic.moderation-result-noti.name}")
    private String moderationResultNotiTopicName;
    @Value("${kafka.topic.moderate.name}")
    private String moderationTopicName;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
    @Bean
    public JsonMessageConverter jsonMessageConverter(){
        return new JsonMessageConverter();
    }
    @Bean
    public NewTopic interactTopic(){
        return new NewTopic(notificationName, 2, (short) 1);
    }
    @Bean
    public NewTopic syncPost(){
        return new NewTopic(syncPostTopicName, 2, (short) 1);
    }
    @Bean
    public NewTopic moderationResultNotiTopic(){
        return new NewTopic(moderationResultNotiTopicName, 2, (short) 1);
    }
    @Bean
    public NewTopic moderationTopic(){
        return new NewTopic(moderationTopicName, 2, (short) 1);
    }
}
