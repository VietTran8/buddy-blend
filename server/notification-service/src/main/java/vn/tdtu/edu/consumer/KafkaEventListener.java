package vn.tdtu.edu.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.FriendRequestMessage;
import vn.tdtu.edu.dto.MailDetails;
import vn.tdtu.edu.dto.Message;
import vn.tdtu.edu.enums.EModerateType;
import vn.tdtu.edu.enums.ENotificationType;
import vn.tdtu.edu.message.CommonNotificationMessage;
import vn.tdtu.edu.message.ModerateResultsMessage;
import vn.tdtu.edu.message.SendOTPMailMessage;
import vn.tdtu.edu.message.newpost.NewPostMessage;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.model.UserInfo;
import vn.tdtu.edu.model.Violation;
import vn.tdtu.edu.service.NotificationSender;
import vn.tdtu.edu.service.interfaces.MailService;
import vn.tdtu.edu.service.interfaces.NotificationService;
import vn.tdtu.edu.service.interfaces.ViolationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventListener {
    private final NotificationSender notificationSender;
    private final NotificationService notiService;
    private final MailService mailService;
    private final ViolationService violationService;

    @KafkaListener(groupId = "InteractNotification", topics = {"${kafka.topic.interact-noti.name}", "${kafka.topic.invite-users-noti.name}"})
    public void consumeInteractTopic(CommonNotificationMessage notification) {
        log.info("Interaction message: " + notification.toString());
        boolean sendResult = notificationSender.sendCommonNotification(notification);

        CommonNotification commonNotification = new CommonNotification();
        commonNotification.setTitle(notification.getTitle());
        commonNotification.setContent(notification.getContent());
        commonNotification.setType(notification.getType());
        commonNotification.setCreateAt(notification.getCreateAt());
        commonNotification.setFromUserId(notification.getFromUserId());
        commonNotification.setToUsers(notification.getToUserIds().stream().map(
                id -> new UserInfo(id, false)
        ).collect(Collectors.toList()));
        commonNotification.setRefId(notification.getRefId());

        notiService.save(commonNotification);

        if (sendResult)
            log.info("Notification sent to target user");
        else
            log.info("Can not send notification to the target user");
    }

    @KafkaListener(groupId = "ChattingNotification", topics = "${kafka.topic.chatting.name}")
    public void consumeChattingTopic(Message message) {
        log.info("Chatting message: " + message.toString());
        if (notificationSender.sendChatNotification(message)) {
            log.info("Message sent to target user");
        } else {
            log.info("Can not send message to the target user");
        }
    }

    @KafkaListener(groupId = "FriendRequestNotification", topics = "${kafka.topic.friend-request.name}")
    public void consumeFriendRequest(FriendRequestMessage message) {
        log.info("friend request message: " + message.toString());

        CommonNotification commonNotification = new CommonNotification();
        commonNotification.setTitle(message.getTitle());
        commonNotification.setContent(message.getContent());
        commonNotification.setType(ENotificationType.FRIEND_REQUEST);
        commonNotification.setCreateAt(String.valueOf(System.currentTimeMillis()));
        commonNotification.setFromUserId(message.getFromUserId());
        commonNotification.setToUsers(List.of(new UserInfo(message.getToUserId(), false)));

        notiService.save(commonNotification);

        notificationSender.sendCommonNotification(commonNotification);
    }

    @KafkaListener(groupId = "ModerateNotiGroup", topics = "${kafka.topic.moderation-result-noti.name}")
    public void consumeModerationResult(ModerateResultsMessage message) {
        log.info("Moderate result message: " + message.toString());

        Violation violation = new Violation();
        violation.setContent(message.getRejectReason());
        violation.setRefId(message.getRefId());

        violationService.save(violation);

        CommonNotification commonNotification = new CommonNotification();
        commonNotification.setTitle("Có vấn đề ở bài viết bạn đã đăng");
        commonNotification.setContent(
                String.format(
                        "Chúng tôi phát hiện có một vài hình ảnh hoặc nội dung không phù hợp trong %s của bạn. Vì vậy, bài viết của bạn đã bị xóa khỏi cộng đồng!",
                        EModerateType.TYPE_POST.equals(message.getType()) ? "bài viết" : "bình luận"
                )
        );
        commonNotification.setType(ENotificationType.MODERATION);
        commonNotification.setCreateAt(message.getTimestamp());
        commonNotification.setToUsers(List.of(new UserInfo(message.getToUserId(), false)));
        commonNotification.setRefId(violation.getId());

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

    @KafkaListener(groupId = "NewPostTopicGroup", topics = "${kafka.topic.new-post.name}")
    public void consumeNewPostTopic(NewPostMessage message) {
        notificationSender.sendNewPostNotification(message);
    }
}
