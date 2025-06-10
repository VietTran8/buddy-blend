package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.UserDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    @Override
    public List<UserDTO> getUsersByIds(String accessToken, List<String> userIds) {
        ResDTO<List<UserDTO>> response = userClient.findByIds(accessToken, new FindByIdsRequest(userIds));
        log.info(response.toString());

        return response.getData();
    }

    @Override
    public UserDTO getUserById(String accessToken, String userId) {
        ResDTO<UserDTO> response = userClient.findById(accessToken, userId);
        log.info(response.toString());

        return response.getData();
    }

    @Override
    public List<UserDTO> getUserFriends(String accessToken) {
        ResDTO<List<UserDTO>> response = userClient.findUserFriendIdsByUserToken(accessToken);
        log.info(response.toString());

        return response.getData();
    }
}
