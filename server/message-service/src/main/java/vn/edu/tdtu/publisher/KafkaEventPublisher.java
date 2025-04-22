package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.MessageNoti;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.chatting.name}")
    private String chattingTopicName;

    public void publishMessageNoti(MessageNoti message) {
        kafkaTemplate.send(chattingTopicName, message);
    }
}
