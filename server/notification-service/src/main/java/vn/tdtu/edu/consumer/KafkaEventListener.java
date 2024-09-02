package vn.tdtu.edu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dtos.FriendRequestNoti;
import vn.tdtu.edu.dtos.Message;
import vn.tdtu.edu.model.InteractNotification;
import vn.tdtu.edu.service.FirebaseService;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventListener {
    private final FirebaseService firebaseService;

    @KafkaListener(groupId = "InteractNotification", topics = "${kafka.topic.interact-noti.name}")
    public void consumeInteractTopic(InteractNotification notification){
        log.info("Interaction message: " + notification.toString());
        boolean sendResult = firebaseService.sendInteractNotification(notification);
        if (sendResult)
            log.info("Message sent to target user");
        else
            log.info("Can not send message to the target user");
    }

    @KafkaListener(groupId = "ChattingNotification", topics = "${kafka.topic.chatting.name}")
    public void consumeChattingTopic(Message message){
        log.info("Chatting message: " + message.toString());
        if(firebaseService.sendChatNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Can not send message to the target user");
        }
    }

    @KafkaListener(groupId = "FriendRequestNotification", topics = "${kafka.topic.friend-request.name}")
    public void consumeFriendRequest(FriendRequestNoti message){
        log.info("Friend request message: " + message.toString());
        if(firebaseService.sendFriendRequestNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Message sent to target user");
        }
    }
}
