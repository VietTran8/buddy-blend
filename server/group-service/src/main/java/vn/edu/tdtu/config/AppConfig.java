package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${kafka.topic.invite-users-noti.name}")
    private String topicName;

    @Bean
    public NewTopic inviteUserTopic() {
        return new NewTopic(topicName, 2, (short) 1);
    }
}
