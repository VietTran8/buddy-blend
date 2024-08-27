package vn.edu.tdtu.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.FindByIdsReq;
import vn.edu.tdtu.dtos.request.FindByIdsReqDTO;
import vn.edu.tdtu.dtos.response.PostResponse;

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

    public List<PostResponse> findByIds(String token, FindByIdsReq reqDTO) {
        String url = UriComponentsBuilder
                .fromHttpUrl(postServiceHost + "api/v1/posts/find-all")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<FindByIdsReq> entity = new HttpEntity<>(reqDTO, headers);

        try {
            ResponseEntity<ResDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, ResDTO.class
            );
            List<PostResponse> postObjects = new ArrayList<>();
            if (response.getBody() != null && response.getBody().getData() != null) {
                postObjects = objectMapper.convertValue(
                        response.getBody().getData(),
                        new TypeReference<List<PostResponse>>() {}
                );
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
