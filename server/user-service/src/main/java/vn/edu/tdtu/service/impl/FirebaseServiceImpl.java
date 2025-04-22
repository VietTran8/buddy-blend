package vn.edu.tdtu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.dto.request.FCMRegistrationIdsBody;
import vn.edu.tdtu.dto.response.FcmResponse;
import vn.edu.tdtu.enums.ERIDHandleType;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.service.interfaces.FirebaseService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {
    @Value("${fcm.sender.id}")
    private String projectId;
    private final WebClient webClient;
    private final static String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

    @Override
    public String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource("service-account.json").getInputStream())
                    .createScoped(Arrays.asList(SCOPES));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();

        } catch (IOException e) {
            log.info(e.getMessage());
            return "";
        }
    }

    @Override
    public void removeUserRegistrationId(User user, List<String> registrationIds) {
        String notificationKey = user.getNotificationKey();

        if (notificationKey != null && !notificationKey.isEmpty()) {
            user.setNotificationKey(handleRegistrationIds(ERIDHandleType.TYPE_REMOVE, user.getId(), registrationIds));
        }

    }

    @Override
    public void saveUserDeviceGroup(User user, List<String> registrationIds) {
        String notificationKey = user.getNotificationKey();

        //If user already have notification key, then add
        if (notificationKey != null && !notificationKey.isEmpty()) {
            handleRegistrationIds(ERIDHandleType.TYPE_ADD, user.getId(), registrationIds);
            return;
        }

        user.setNotificationKey(handleRegistrationIds(ERIDHandleType.TYPE_CREATE, user.getId(), registrationIds));
        log.info(user.getNotificationKey());

    }

    @Override
    public FcmResponse getNotificationKey(String notificationKeyName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fcm/notification")
                        .queryParam("notification_key_name", notificationKeyName)
                        .build())
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Content-Type", "application/json")
                .header("access_token_auth", "true")
                .header("project_id", projectId)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.value() == 400, clientResponse -> Mono.empty())
                .bodyToMono(FcmResponse.class)
                .block();
    }

    private String handleRegistrationIds(ERIDHandleType type, String notiKeyName, List<String> regisIds) {
        String notificationKey = getNotificationKey(notiKeyName).getNotification_key();

        String notificationUrl = "https://fcm.googleapis.com/fcm/notification";
        String serverKey = getAccessToken();

        FCMRegistrationIdsBody requestBody = new FCMRegistrationIdsBody();
        requestBody.setRegistration_ids(regisIds);
        requestBody.setNotification_key_name(notiKeyName);
        requestBody.setOperation(type.getName());

        if (type == ERIDHandleType.TYPE_ADD || type == ERIDHandleType.TYPE_REMOVE) {
            log.info("Notification key: " + notificationKey);
            requestBody.setNotification_key(notificationKey);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            log.info("Project id: " + projectId);
            log.info("Access token: " + serverKey);
            HttpPost httpPost = new HttpPost(notificationUrl);
            httpPost.setHeader("Authorization", "Bearer " + serverKey);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("project_id", projectId);
            httpPost.setHeader("access_token_auth", true);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            httpPost.setEntity(new StringEntity(jsonBody));

            String responseBody = httpClient.execute(httpPost, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

            JsonNode root = objectMapper.readTree(responseBody);
            log.info("Response body: " + responseBody);

            if (root.path("notification_key").asText().isEmpty()) {
                log.error("Failed to send notification request: " + root.path("error").asText());
                return "";
            }

            return getNotificationKey(notiKeyName).getNotification_key();

        } catch (Exception e) {
            log.error("Failed to send notification request", e);
            return "";
        }
    }
}