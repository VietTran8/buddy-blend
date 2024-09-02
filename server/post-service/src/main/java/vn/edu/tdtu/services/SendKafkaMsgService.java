package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.response.InteractNotification;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.message.SyncPostMsg;
import vn.edu.tdtu.models.Post;

@Service
@RequiredArgsConstructor
public class SendKafkaMsgService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.interact-noti.name}")
    private  String interactTopicName;
    @Value("${kafka.topic.sync-post.name}")
    private  String syncPostTopicName;

    public void pubSharePostMessage(InteractNotification notification){
        kafkaTemplate.send(interactTopicName, notification);
    }

    public void pubSyncPostMessage(Post post, ESyncType syncType){
        kafkaTemplate.send(syncPostTopicName, SyncPostMsg.fromModel(post, syncType));
    }
}
