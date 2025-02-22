package vn.tdtu.edu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.tdtu.edu.message.UserConnectMessage;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.user-connected.name}")
    private String userConnectedTopicName;
    @Value("${kafka.topic.user-disconnected.name}")
    private String userDisconnectedTopicName;

    public void publishUserConnected (UserConnectMessage message) {
        kafkaTemplate.send(userConnectedTopicName, message);
    }

    public void publishUserDisconnected (UserConnectMessage message) {
        kafkaTemplate.send(userDisconnectedTopicName, message);
    }
}
