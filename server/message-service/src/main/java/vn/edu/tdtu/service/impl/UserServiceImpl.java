package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    @Override
    public UserDTO findById(String userId) {
        ResponseVM<UserDTO> response = userClient.findById(userId);
        return response.getData();
    }
}