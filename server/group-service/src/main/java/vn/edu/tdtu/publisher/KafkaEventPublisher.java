package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.Notification;
import vn.edu.tdtu.message.SyncGroupMsg;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.invite-users-noti.name}")
    private String inviteUserNotiTopicName;
    @Value("${kafka.topic.sync-group.name}")
    private String syncGroupTopicName;

    public void publishInviteUsers(Notification notification) {
        kafkaTemplate.send(inviteUserNotiTopicName, notification);
    }

    public void publishSyncGroupData(SyncGroupMsg msg) {
        kafkaTemplate.send(syncGroupTopicName, msg);
    }
}
