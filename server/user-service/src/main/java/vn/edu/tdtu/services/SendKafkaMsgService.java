package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.response.FriendRequestNoti;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.message.SyncUserMsg;
import vn.edu.tdtu.models.User;

@Service
@RequiredArgsConstructor
public class SendKafkaMsgService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.friend-request.name}")
    private  String friendRequestTopicName;
    @Value("${kafka.topic.sync-user.name}")
    private  String syncUserTopicName;

    public void pubFriendRequestNoti(FriendRequestNoti notification){
        kafkaTemplate.send(friendRequestTopicName, notification);
    }

    public void pubSyncUserData(User user, ESyncType syncType) {
        kafkaTemplate.send(syncUserTopicName, SyncUserMsg.fromModel(user, syncType));
    }
}
