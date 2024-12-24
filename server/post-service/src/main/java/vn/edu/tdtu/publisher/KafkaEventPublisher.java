package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.message.ModerateImagesMessage;
import vn.edu.tdtu.message.ModerationNotificationMsg;
import vn.edu.tdtu.message.SyncPostMsg;
import vn.edu.tdtu.model.Post;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.interact-noti.name}")
    private  String interactTopicName;
    @Value("${kafka.topic.sync-post.name}")
    private  String syncPostTopicName;
    @Value("${kafka.topic.moderation-result-noti.name}")
    private String moderateNotificationTopicName;
    @Value("${kafka.topic.moderate.name}")
    private String moderateTopicName;

    public void pubSharePostMessage(InteractNotification notification){
        kafkaTemplate.send(interactTopicName, notification);
    }

    public void pubSyncPostMessage(Post post, ESyncType syncType){
        kafkaTemplate.send(syncPostTopicName, SyncPostMsg.fromModel(post, syncType));
    }

    public void pubModerateResultNotificationMessage(ModerationNotificationMsg msg){
        kafkaTemplate.send(moderateNotificationTopicName, msg);
    }

    public void pubModerateImagesMessage(ModerateImagesMessage msg){
        kafkaTemplate.send(moderateTopicName, msg);
    }
}
