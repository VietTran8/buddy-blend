package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.tdtu.common.enums.search.ESyncType;
import vn.edu.tdtu.message.FriendRequestMessage;
import vn.edu.tdtu.message.SyncUserMsg;
import vn.edu.tdtu.model.User;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.friend-request.name}")
    private String friendRequestTopicName;
    @Value("${kafka.topic.sync-user.name}")
    private String syncUserTopicName;

    public void pubFriendRequestNoti(FriendRequestMessage notification) {
        kafkaTemplate.send(friendRequestTopicName, notification);
    }

    public void pubSyncUserData(User user, ESyncType syncType) {
        kafkaTemplate.send(syncUserTopicName, SyncUserMsg.fromModel(user, syncType));
    }
}
