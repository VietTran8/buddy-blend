package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.response.InteractNotification;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.interact-noti.name}")
    private String notificationTopic;

    public void publishInteractNoti(InteractNotification notification) {
        kafkaTemplate.send(notificationTopic, notification);
    }
}
