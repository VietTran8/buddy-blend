package vn.edu.tdtu.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    @Value("${service.post-service.host}")
    private String postServiceHost;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<Post> findByContentContaining(String token, String key) {
        String url = UriComponentsBuilder
                .fromHttpUrl(postServiceHost + "api/v1/posts/search")
                .queryParam("key", key)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ResDTO.class);
            List<Post> postObjects = new ArrayList<>();
            if (response.getBody() != null && response.getBody().getData() != null) {
                postObjects = objectMapper.convertValue(response.getBody().getData(), new TypeReference<List<Post>>() {});
                System.out.println("[Fetched] response data: " + response.getBody().getData());
            }
            return postObjects;
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("General Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
