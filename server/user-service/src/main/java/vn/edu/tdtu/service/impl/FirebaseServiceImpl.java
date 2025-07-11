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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vn.edu.tdtu.dto.request.FCMRegistrationIdsBody;
import vn.edu.tdtu.dto.response.FcmResponse;
import vn.edu.tdtu.enums.ERIDHandleType;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.service.interfaces.FirebaseService;
import vn.tdtu.common.utils.Constants;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {
    private final WebClient webClient;

    @Override
    public String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource("service-account.json").getInputStream())
                    .createScoped(List.of(Constants.Firebase.SCOPES));

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
                        .queryParam(Constants.Firebase.QUERY_PARAM_NOTIFICATION_KEY_NAME, notificationKeyName)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, Constants.BEARER_PREFIX + getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(Constants.Firebase.ACCESS_TOKEN_AUTH_HEADER, "true")
                .header(Constants.Firebase.PROJECT_ID_HEADER, Constants.Firebase.PROJECT_SENDER_ID)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.value() == 400, clientResponse -> Mono.empty())
                .bodyToMono(FcmResponse.class)
                .block();
    }

    private String handleRegistrationIds(ERIDHandleType type, String notiKeyName, List<String> regisIds) {
        String notificationKey = getNotificationKey(notiKeyName).getNotificationKey();
        String serverKey = getAccessToken();

        FCMRegistrationIdsBody requestBody = new FCMRegistrationIdsBody();
        requestBody.setRegistrationIds(regisIds);
        requestBody.setNotificationKeyName(notiKeyName);
        requestBody.setOperation(type.getName());

        if (type == ERIDHandleType.TYPE_ADD || type == ERIDHandleType.TYPE_REMOVE) {
            log.info("[handleRegistrationIds] Notification key: " + notificationKey);
            requestBody.setNotificationKey(notificationKey);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            log.info("Project id: " + Constants.Firebase.PROJECT_SENDER_ID);
            log.info("Access token: " + serverKey);

            HttpPost httpPost = new HttpPost(Constants.Firebase.NOTIFICATION_URl);

            httpPost.setHeader(HttpHeaders.AUTHORIZATION, Constants.BEARER_PREFIX + serverKey);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpPost.setHeader(Constants.Firebase.PROJECT_ID_HEADER, Constants.Firebase.PROJECT_SENDER_ID);
            httpPost.setHeader(Constants.Firebase.ACCESS_TOKEN_AUTH_HEADER, true);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            httpPost.setEntity(new StringEntity(jsonBody));

            String responseBody = httpClient.execute(httpPost, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

            JsonNode root = objectMapper.readTree(responseBody);
            log.info("Response body: " + responseBody);

            if (root.path(Constants.Firebase.RESPONSE_BODY_NOTIFICATION_KEY).asText().isEmpty()) {
                log.error("Failed to send notification request: {}",
                        root.path(Constants.Firebase.RESPONSE_BODY_ERROR).asText());
                return "";
            }

            return getNotificationKey(notiKeyName).getNotificationKey();

        } catch (Exception e) {
            log.error("Failed to send notification request", e);
            return "";
        }
    }
}