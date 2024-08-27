package vn.edu.tdtu.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.models.Post;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    @Value("${service.post-service.host}")
    private String postServiceHost;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Post findById(String token, String postId){
        String getUserInfoUrl = UriComponentsBuilder
                .fromHttpUrl(postServiceHost + "api/v1/posts/" + postId)
                .toUriString();

        log.info(getUserInfoUrl);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<ResDTO> responseEntity = restTemplate.exchange(
                    getUserInfoUrl,
                    HttpMethod.GET,
                    entity,
                    ResDTO.class);
            ResDTO<?> response = responseEntity.getBody();
            Post postObject = new Post();
            if(response != null && response.getData() != null) {
                postObject = objectMapper.convertValue(response.getData(), Post.class);
                log.info("[Fetched] response data: " + response.getData());
            }

            return postObject;
        }catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}

