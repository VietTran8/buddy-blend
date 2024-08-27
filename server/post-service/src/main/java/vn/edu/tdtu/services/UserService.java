package vn.edu.tdtu.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.models.User;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${service.user-service.host}")
    private String userServiceHost;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public User findById(String accessToken, String userId) {
        String getUserInfoUrl = UriComponentsBuilder
                .fromHttpUrl(userServiceHost + "api/v1/users/" + userId)
                .toUriString();

        log.info(getUserInfoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResDTO<User>> responseEntity = restTemplate.exchange(
                    getUserInfoUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ResDTO<User>>() {}
            );

            User userObject = null;
            if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
                userObject = responseEntity.getBody().getData();
                log.info("[Fetched] response data: " + userObject);
            }

            return userObject;
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<User> findByIds(String accessToken, List<String> ids) {
        String getUserInfoUrl = UriComponentsBuilder
                .fromHttpUrl(userServiceHost + "api/v1/users/by-ids")
                .toUriString();

        log.info(getUserInfoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("userIds", ids);

        HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<ResDTO<List<User>>> responseEntity = restTemplate.exchange(
                    getUserInfoUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ResDTO<List<User>>>() {}
            );

            List<User> userObjects = new ArrayList<>();
            if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
                userObjects = responseEntity.getBody().getData();  // Lấy dữ liệu trực tiếp
                log.info("[Fetched] response data: " + userObjects);
            }

            return userObjects;
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<User> findUserFriendIdsByUserToken(String token) {
        String getUserFriendsUrl = UriComponentsBuilder
                .fromHttpUrl(userServiceHost + "api/v1/users/friends")
                .toUriString();

        log.info(getUserFriendsUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResDTO> responseEntity = restTemplate.exchange(
                    getUserFriendsUrl,
                    HttpMethod.GET,
                    entity,
                    ResDTO.class);

            ResDTO<?> response = responseEntity.getBody();
            List<User> userObjects = new ArrayList<>();
            if (response != null && response.getData() != null) {
                userObjects = objectMapper.convertValue(response.getData(), new TypeReference<List<User>>() {});
                log.info("[Fetched] response data: " + response.getData());
            }

            return userObjects;
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}