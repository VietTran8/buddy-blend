package vn.edu.tdtu.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.message.ModerateImagesResultsMessage;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.moderation-result.name}")
    private String moderateTopicName;

    public void publishModerationResult(ModerateImagesResultsMessage message) {
        kafkaTemplate.send(moderateTopicName, message);
    }
}
