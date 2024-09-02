package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.requests.user.FindByIdsRequest;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.httpclient.UserClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    public User findById(String accessToken, String userId) {
        ResDTO<User> response = userClient.findById(accessToken, userId);
        return response.getData();
    }

    public List<User> findByIds(String accessToken, List<String> ids) {
        ResDTO<List<User>> response = userClient.findByIds(
                accessToken,
                new FindByIdsRequest(ids)
        );

         log.info(response.toString());

        return response.getData();
    }
}