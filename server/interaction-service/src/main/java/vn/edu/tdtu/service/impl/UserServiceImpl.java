package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.requests.user.FindByIdsRequest;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    @Override
    public UserDTO findById(String accessToken, String userId) {
        ResponseVM<UserDTO> response = userClient.findById(accessToken, userId);
        return response.getData();
    }

    @Override
    public List<UserDTO> findByIds(String accessToken, List<String> ids) {
        ResponseVM<List<UserDTO>> response = userClient.findByIds(
                accessToken,
                new FindByIdsRequest(ids)
        );

        log.info(response.toString());

        return response.getData();
    }
}