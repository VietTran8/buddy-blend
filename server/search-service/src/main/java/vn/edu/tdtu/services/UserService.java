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
import vn.edu.tdtu.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${service.user-service.host}")
    private String userServiceHost;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<User> findByNameContaining(String accessToken, String name) {
        String getUserInfoUrl = UriComponentsBuilder
                .fromHttpUrl(userServiceHost + "api/v1/users/search")
                .queryParam("key", name)
                .toUriString();

        log.info(getUserInfoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResDTO<List<User>>> responseEntity = restTemplate.exchange(getUserInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<ResDTO<List<User>>>() {});
            List<User> userObjects = new ArrayList<>();
            if (responseEntity.getBody() != null && responseEntity.getBody().getData() != null) {
                userObjects = objectMapper.convertValue(responseEntity.getBody().getData(), new TypeReference<List<User>>() {});
                log.info("[Fetched] response data: " + responseEntity.getBody().getData());
            }

            return userObjects;
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    public User findById(String userId){
        String getUserInfoUrl = UriComponentsBuilder
                .fromHttpUrl(userServiceHost + "api/v1/users/" + userId)
                .toUriString();

        log.info(getUserInfoUrl);

        try {
            ResDTO<?> response = restTemplate.getForObject(getUserInfoUrl, ResDTO.class);
            User userObject = new User();
            if(response != null && response.getData() != null){
                userObject = objectMapper.convertValue(response.getData(), new TypeReference<User>(){});
                log.info("[Fetched] response data: " + response.getData());
            }

            return userObject;
        }catch (HttpClientErrorException e){
            log.error(e.getMessage());
            return null;
        }
    }
}
