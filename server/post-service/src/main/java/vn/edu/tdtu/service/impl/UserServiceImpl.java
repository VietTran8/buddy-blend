package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.user.FindByIdsRequest;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.intefaces.UserService;
import vn.tdtu.common.dto.UserDTO;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    @Override
    @Cacheable(key = "T(java.util.Objects).hash(#a0, #a1)", value = "single-user", unless = "#result == null")
    public UserDTO findById(String accessToken, String userId) {
        ResDTO<UserDTO> response = userClient.findById(accessToken, userId);
        log.info("findById: " + response.toString());

        return response.getData();
    }

    @Override
    public List<UserDTO> findByIds(String accessToken, List<String> ids) {
        ResDTO<List<UserDTO>> response = userClient.findByIds(accessToken, new FindByIdsRequest(ids));
        log.info("findByIds: " + response.toString());

        return response.getData();
    }

    @Override
    public List<UserDTO> findUserFriendIdsByUserToken(String token) {
        ResDTO<List<UserDTO>> response = userClient.findUserFriendIdsByUserToken(token);
        log.info("findFriends: " + response.toString());

        return response.getData();
    }

    @Override
    public List<String> findUserFriendIdsByUserId(String token, String userId) {
        ResDTO<List<String>> response = userClient.findUserFriendIdsByUserId(token, userId);
        log.info("findFriendIds: " + response.toString());

        return response.getData();
    }
}