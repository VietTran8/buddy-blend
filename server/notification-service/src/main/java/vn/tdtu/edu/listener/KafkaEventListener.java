package vn.tdtu.edu.listener;

import lombok.RequiredArgsConstructor;
import vn.tdtu.edu.dtos.FriendRequestNoti;
import vn.tdtu.edu.model.InteractNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dtos.Message;
import vn.tdtu.edu.service.FirebaseService;
import vn.tdtu.edu.service.PostService;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventListener {
    private final FirebaseService firebaseService;
    private final PostService postService;
    @KafkaListener(groupId = "InteractNotification", topics = "interact-noti")
    public void consumeInteractTopic(InteractNotification notification){
        log.info("Interaction message: " + notification.toString());
        boolean sendResult = firebaseService.sendInteractNotification(notification);
        if (sendResult)
            log.info("Message sent to target user");
        else
            log.info("Can not send message to the target user");
    }

    @KafkaListener(groupId = "ChattingNotification", topics = "chatting")
    public void consumeChattingTopic(Message message){
        log.info("Chatting message: " + message.toString());
        if(firebaseService.sendChatNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Can not send message to the target user");
        }
    }

    @KafkaListener(groupId = "FriendRequestNotification", topics = "friend-request")
    public void consumeFriendRequest(FriendRequestNoti message){
        log.info("Friend request message: " + message.toString());
        if(firebaseService.sendFriendRequestNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Message sent to target user");
        }
    }
}
