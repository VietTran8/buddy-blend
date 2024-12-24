package vn.tdtu.edu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.*;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.model.data.User;
import vn.tdtu.edu.service.interfaces.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {
    private final UserService userService;
    private final SocketModule socketModule;
    private final static String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";
    @Value("${fcm.project.id}")
    private String projectId;

    public String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource("service-account.json").getInputStream())
                    .createScoped(List.of(SCOPES));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();

        } catch (IOException e) {
            log.info(e.getMessage());
            return "";
        }
    }

    public boolean sendCommonNotification(CommonNotification commonNotification){
        socketModule.emitNotification(commonNotification);

        String SEND_NOTI_URL = "https://fcm.googleapis.com/v1/projects/"+ projectId +"/messages:send";

        User foundUser = userService.findById(commonNotification.getToUserIds().get(0));
        if (foundUser != null) {
            String notificationKey = foundUser.getNotificationKey();
            if(notificationKey == null || notificationKey.isEmpty()){
                return false;
            }

            String token = getAccessToken();

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(SEND_NOTI_URL);
                httpPost.setHeader("Authorization", "Bearer " + token);
                httpPost.setHeader("Content-Type", "application/json");

                ObjectMapper objectMapper = new ObjectMapper();
                NotificationRequestBody<CommonNotification> requestBody = new NotificationRequestBody<>();
                NotificationMessage<CommonNotification> message = new NotificationMessage<>();
                requestBody.setMessage(message);

                NotificationContent notificationContent = new NotificationContent();
                notificationContent.setTitle("Có người tương tác nè!!");
                notificationContent.setBody(commonNotification.getContent());
                notificationContent.setImage(commonNotification.getAvatarUrl());

                message.setNotification(notificationContent);
                message.setData(commonNotification);
                message.setToken(notificationKey);

                String jsonBody = objectMapper.writeValueAsString(requestBody);

                httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

                return httpClient.execute(httpPost, response -> {
                    int status = response.getStatusLine().getStatusCode();
                    if (status == 200) {
                        log.info("Notification sent successfully.");
                        return true;
                    } else {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        log.info("Failed to send notification. Status code: " + status);
                        log.info("Response Body: " + responseBody);
                        return false;
                    }
                });
            } catch (Exception e) {
                log.error("Error sending notification", e);
                return false;
            }
        }

        return false;
    }

    public boolean sendChatNotification(Message message){
        String SEND_NOTI_URL = "https://fcm.googleapis.com/v1/projects/"+ projectId +"/messages:send";

        String toUserId = message.getToUserId();
        String fromUserId = message.getFromUserId();

        List<User> users = userService.findByIds(List.of(message.getToUserId(), message.getFromUserId()));
        if (users == null || users.isEmpty()) {
            return false;
        }

        User toUser = users.stream().filter(user -> user.getId().equals(toUserId)).findFirst().orElse(null);
        User fromUser = users.stream().filter(user -> user.getId().equals(fromUserId)).findFirst().orElse(null);

        if(toUser == null){
            log.error("Failed to send to a null user");
            return false;
        }

        NewMessageNoti messageNoti = new NewMessageNoti(message, getUserFullName(fromUser), fromUser.getProfilePicture());
        socketModule.emitChatNotification(messageNoti);

        String notificationKey = toUser.getNotificationKey();
        if(notificationKey == null || notificationKey.isEmpty()){
            return false;
        }

        String token = getAccessToken();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(SEND_NOTI_URL);
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Content-Type", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            NotificationRequestBody<Message> requestBody = new NotificationRequestBody<>();
            NotificationMessage<Message> notificationMessage = new NotificationMessage<>();
            requestBody.setMessage(notificationMessage);

            NotificationContent notificationContent = new NotificationContent();
            notificationContent.setTitle(getUserFullName(fromUser));
            notificationContent.setBody(message.getContent());
            notificationContent.setImage(getUserAvatar(fromUser));

            notificationMessage.setNotification(notificationContent);
            notificationMessage.setData(messageNoti);
            notificationMessage.setToken(notificationKey);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            return httpClient.execute(httpPost, response -> {
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    log.info("Notification sent successfully.");
                    return true;
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                log.info("Failed to send notification. Status code: " + status);
                log.info("Response Body: " + responseBody);
                return false;
            });
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return false;
        }
    }

    private String getUserFullName(User foundUser){
        return foundUser != null ? String.join(" ", foundUser.getFirstName(), foundUser.getMiddleName(), foundUser.getLastName()) : "Unknown";
    }

    private String getUserAvatar(User foundUser){
        return foundUser != null ? foundUser.getProfilePicture() : "Unknown";
    }
}