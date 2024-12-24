package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    public List<User> getUsersByIds(String accessToken, List<String> userIds){
        ResDTO<List<User>> response =  userClient.findByIds(accessToken, new FindByIdsRequest(userIds));
        log.info(response.toString());

        return response.getData();
    }

    public User getUserById(String accessToken, String userId){
        ResDTO<User> response =  userClient.findById(accessToken, userId);
        log.info(response.toString());

        return response.getData();
    }

    public List<User> getUserFriends(String accessToken){
        ResDTO<List<User>> response =  userClient.findUserFriendIdsByUserToken(accessToken);
        log.info(response.toString());

        return response.getData();
    }
}
