package vn.edu.tdtu.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.message.ModerateMessage;
import vn.edu.tdtu.message.ModerationNotificationMsg;
import vn.edu.tdtu.message.NewPostMessage;
import vn.edu.tdtu.message.SyncPostMsg;
import vn.edu.tdtu.model.Post;
import vn.tdtu.common.enums.search.ESyncType;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.interact-noti.name}")
    private String interactTopicName;
    @Value("${kafka.topic.sync-post.name}")
    private String syncPostTopicName;
    @Value("${kafka.topic.moderation-result-noti.name}")
    private String moderateNotificationTopicName;
    @Value("${kafka.topic.moderate.name}")
    private String moderateTopicName;
    @Value("${kafka.topic.new-post.name}")
    private String newPostTopicName;

    public void pubSharePostMessage(InteractNotification notification) {
        kafkaTemplate.send(interactTopicName, notification);
    }

    public void pubSyncPostMessage(Post post, ESyncType syncType) {
        kafkaTemplate.send(syncPostTopicName, SyncPostMsg.fromModel(post, syncType));
    }

    public void pubModerateResultNotificationMessage(ModerationNotificationMsg msg) {
        kafkaTemplate.send(moderateNotificationTopicName, msg);
    }

    public void pubModerateMessage(ModerateMessage msg) {
        kafkaTemplate.send(moderateTopicName, msg);
    }

    public void pubNewPostMessage(NewPostMessage msg) {
        kafkaTemplate.send(newPostTopicName, msg);
    }
}
