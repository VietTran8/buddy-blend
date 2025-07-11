package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${kafka.topic.interact-noti.name}")
    private String notificationName;

    @Bean
    public NewTopic interactNoti() {
        return new NewTopic(notificationName, 2, (short) 1);
    }

}
