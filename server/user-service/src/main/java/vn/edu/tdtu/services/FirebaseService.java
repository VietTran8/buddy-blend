package vn.edu.tdtu.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.dtos.request.FCMRegistrationIdsBody;
import vn.edu.tdtu.dtos.response.FcmResponse;
import vn.edu.tdtu.enums.ERIDHandleType;
import vn.edu.tdtu.models.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseService {
    @Value("${fcm.sender.id}")
    private String projectId;
    private final WebClient webClient;
    private final static String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";
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

    public String removeUserRegistrationId(User user, List<String> registrationIds) {
        String notificationKey = user.getNotificationKey();

        if(notificationKey != null && !notificationKey.isEmpty()) {
            user.setNotificationKey(handleRegistrationIds(ERIDHandleType.TYPE_REMOVE, user.getId(), registrationIds));
        }

        return user.getNotificationKey();
    }

    public String saveUserDeviceGroup(User user, List<String> registrationIds){
        String notificationKey = user.getNotificationKey();

        //If user already have notification key, then add
        if(notificationKey != null && !notificationKey.isEmpty()){
            return handleRegistrationIds(ERIDHandleType.TYPE_ADD, user.getId(), registrationIds);
        }

        user.setNotificationKey(handleRegistrationIds(ERIDHandleType.TYPE_CREATE, user.getId(), registrationIds));
        log.info(user.getNotificationKey());

        return user.getNotificationKey();
    }

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

    private String handleRegistrationIds(ERIDHandleType type, String notiKeyName, List<String> regisIds){
        String notificationKey = getNotificationKey(notiKeyName).getNotification_key();

        String notificationUrl = "https://fcm.googleapis.com/fcm/notification";
        String serverKey = getAccessToken();

        FCMRegistrationIdsBody requestBody = new FCMRegistrationIdsBody();
        requestBody.setRegistration_ids(regisIds);
        requestBody.setNotification_key_name(notiKeyName);
        requestBody.setOperation(type.getName());

        if(type == ERIDHandleType.TYPE_ADD || type == ERIDHandleType.TYPE_REMOVE){
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
            log.info("Response body: "+ responseBody);

            if(root.path("notification_key").asText().isEmpty()){
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