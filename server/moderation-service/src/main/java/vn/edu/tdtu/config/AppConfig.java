package vn.edu.tdtu.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class AppConfig {
    @Value("${kafka.topic.moderation-result.name}")
    private String moderateResultTopicName;

    @Bean
    public NewTopic moderateResultTopic() {
        return new NewTopic(moderateResultTopicName, 2, (short) 1);
    }

    @Bean
    public JsonMessageConverter jsonMessageConverter(){
        return new JsonMessageConverter();
    }
}
