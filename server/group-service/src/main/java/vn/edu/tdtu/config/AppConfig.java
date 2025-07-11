package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${kafka.topic.invite-users-noti.name}")
    private String inviteUserNotiTopicName;
    @Value("${kafka.topic.sync-group.name}")
    private String syncGroupTopicName;

    @Bean
    public NewTopic inviteUserNotiTopic() {
        return new NewTopic(inviteUserNotiTopicName, 2, (short) 1);
    }

    @Bean
    public NewTopic syncGroupTopic() {
        return new NewTopic(syncGroupTopicName, 2, (short) 1);
    }

}
