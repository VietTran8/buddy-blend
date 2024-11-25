package vn.tdtu.edu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dtos.FriendRequestNoti;
import vn.tdtu.edu.dtos.Message;
import vn.tdtu.edu.model.InteractNotification;
import vn.tdtu.edu.service.NotificationService;
import vn.tdtu.edu.service.InteractNotiService;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventListener {
    private final NotificationService notificationService;
    private final InteractNotiService notiService;

    @KafkaListener(groupId = "InteractNotification", topics = {"${kafka.topic.interact-noti.name}", "${kafka.topic.invite-users-noti.name}"})
    public void consumeInteractTopic(InteractNotification notification){
        log.info("Interaction message: " + notification.toString());
        boolean sendResult = notificationService.sendInteractNotification(notification);

        notiService.save(notification);

        if (sendResult)
            log.info("Notification sent to target user");
        else
            log.info("Can not send notification to the target user");
    }

    @KafkaListener(groupId = "ChattingNotification", topics = "${kafka.topic.chatting.name}")
    public void consumeChattingTopic(Message message){
        log.info("Chatting message: " + message.toString());
        if(notificationService.sendChatNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Can not send message to the target user");
        }
    }

    @KafkaListener(groupId = "FriendRequestNotification", topics = "${kafka.topic.friend-request.name}")
    public void consumeFriendRequest(FriendRequestNoti message){
        log.info("Friend request message: " + message.toString());
        if(notificationService.sendFriendRequestNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Message sent to target user");
        }
    }
}
