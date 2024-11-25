package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.InviteUsersRequest;
import vn.edu.tdtu.dto.response.Notification;

@Component
@RequiredArgsConstructor
public class InviteUserPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.invite-users-noti.name}")
    private String topicName;

    public void publishInviteUsers(Notification notification) {
        kafkaTemplate.send(topicName, notification);
    }
}
