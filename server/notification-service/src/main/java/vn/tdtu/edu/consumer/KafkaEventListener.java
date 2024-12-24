package vn.tdtu.edu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.FriendRequestMessage;
import vn.tdtu.edu.dto.MailDetails;
import vn.tdtu.edu.dto.Message;
import vn.tdtu.edu.enums.ENotificationType;
import vn.tdtu.edu.message.ModerateImagesResultsMessage;
import vn.tdtu.edu.message.SendOTPMailMessage;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.service.NotificationSender;
import vn.tdtu.edu.service.interfaces.MailService;
import vn.tdtu.edu.service.interfaces.NotificationService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventListener {
    private final NotificationSender notificationSender;
    private final NotificationService notiService;
    private final MailService mailService;

    @KafkaListener(groupId = "InteractNotification", topics = {"${kafka.topic.interact-noti.name}", "${kafka.topic.invite-users-noti.name}"})
    public void consumeInteractTopic(CommonNotification notification){
        log.info("Interaction message: " + notification.toString());
        boolean sendResult = notificationSender.sendCommonNotification(notification);

        notiService.save(notification);

        if (sendResult)
            log.info("Notification sent to target user");
        else
            log.info("Can not send notification to the target user");
    }

    @KafkaListener(groupId = "ChattingNotification", topics = "${kafka.topic.chatting.name}")
    public void consumeChattingTopic(Message message){
        log.info("Chatting message: " + message.toString());
        if(notificationSender.sendChatNotification(message)){
            log.info("Message sent to target user");
        }else{
            log.info("Can not send message to the target user");
        }
    }

    @KafkaListener(groupId = "FriendRequestNotification", topics = "${kafka.topic.friend-request.name}")
    public void consumeFriendRequest(FriendRequestMessage message){
        log.info("friend request message: " + message.toString());

        CommonNotification commonNotification = new CommonNotification();
        commonNotification.setTitle(message.getTitle());
        commonNotification.setContent(message.getContent());
        commonNotification.setType(ENotificationType.FRIEND_REQUEST);
        commonNotification.setCreateAt(String.valueOf(System.currentTimeMillis()));
        commonNotification.setToUserIds(List.of(message.getToUserId()));
        commonNotification.setUserFullName(message.getUserFullName());

        notiService.save(commonNotification);

        notificationSender.sendCommonNotification(commonNotification);
    }

    @KafkaListener(groupId = "ModerateNotiGroup", topics = "${kafka.topic.moderation-result-noti.name}")
    public void consumeModerationResult(ModerateImagesResultsMessage message){
        log.info("Moderate result message: " + message.toString());

        CommonNotification commonNotification = new CommonNotification();
        commonNotification.setTitle("Có vấn đề ở bài viết bạn đã đăng");
        commonNotification.setContent("Chúng tôi phát hiện có một vài hình ảnh không phù hợp trong bài viết của bạn. Vì vậy, bài viết của bạn đã bị xóa khỏi cộng đồng!");
        commonNotification.setType(ENotificationType.MODERATION);
        commonNotification.setCreateAt(message.getTimestamp());
        commonNotification.setToUserIds(List.of(message.getToUserId()));

        notiService.save(commonNotification);

        notificationSender.sendCommonNotification(commonNotification);
    }

    @KafkaListener(groupId = "SendOtpMailGroup", topics = "${kafka.topic.send-otp-mail.name}")
    public void consumeSendOtpMailTopic(SendOTPMailMessage message) {
        MailDetails mailDetails = new MailDetails();
        mailDetails.setSendTo(message.getEmail());
        mailDetails.setText(String.format("Mã OTP của bạn là: %s", message.getOtp()));
        mailDetails.setSubject("MÃ OTP");

        mailService.sendMail(mailDetails);
    }
}
