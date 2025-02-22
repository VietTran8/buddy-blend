package vn.edu.tdtu.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.message.ModerateResultsMessage;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.moderation-result.name}")
    private String moderateTopicName;

    public void publishModerationResult(ModerateResultsMessage message) {
        kafkaTemplate.send(moderateTopicName, message);
    }
}
