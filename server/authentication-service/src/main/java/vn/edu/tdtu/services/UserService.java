package vn.edu.tdtu.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.SignUpRequest;
import vn.edu.tdtu.dtos.response.SignUpResponse;
import vn.edu.tdtu.models.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${service.user-service.host}")
    private String userServiceHost;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public User getUserInfo(String email){
        String getUserInfoUrl = userServiceHost + "api/v1/users/" + email + "/for-auth";
        try {
            ResDTO<?> response = restTemplate.getForObject(getUserInfoUrl, ResDTO.class);
            User userObject = new User();
            if(response != null && response.getData() != null){
                userObject = objectMapper.convertValue(response.getData(), User.class);
                log.info("[Fetched] response data: " + response.getData());
            }

            return userObject;
        }catch (HttpClientErrorException e){
            log.error(e.getMessage());
            return null;
        }
    }

    public SignUpResponse saveUser(SignUpRequest user){
        String signUpUrl = userServiceHost + "api/v1/users/save";
        try {
            ResDTO<?> response = restTemplate.postForObject(signUpUrl, user, ResDTO.class);
            SignUpResponse responseObject = new SignUpResponse();
            if(response != null && response.getData() != null){
                responseObject = objectMapper.convertValue(response.getData(), SignUpResponse.class);
                log.info("[Fetched] response data: " + response.getData());
            }

            return responseObject;
        }catch (HttpClientErrorException e){
            log.error(e.getMessage());
            return null;
        }
    }
}
