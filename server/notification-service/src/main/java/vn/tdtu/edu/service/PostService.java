package vn.tdtu.edu.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.tdtu.edu.dtos.ResDTO;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    @Value("${service.post-service.host}")
    private String postServiceHost;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String getUserIdByPostId(String postId){
        String getUserInfoUrl = UriComponentsBuilder
                .fromHttpUrl(postServiceHost + "api/v1/posts/user/" + postId)
                .toUriString();

        log.info(getUserInfoUrl);

        try {
            ResDTO<?> response = restTemplate.getForObject(getUserInfoUrl, ResDTO.class);
            Map<String, String> responseData = new HashMap<>();
            if(response != null && response.getData() != null){
                responseData = objectMapper.convertValue(response.getData(), new TypeReference<Map<String, String>>(){});
                log.info("[Fetched] response data: " + response.getData());
            }

            return responseData.get("userId");
        }catch (HttpClientErrorException e){
            log.error(e.getMessage());
            return "";
        }
    }
}
