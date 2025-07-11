package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.SignUpRequest;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    @Override
    public User getUserInfo(String email) {
        try {
            ResponseVM<User> response = userClient.getUserInfo(email);
            return response.getData();
        } catch (Exception e) {
            throw new BadRequestException(MessageCode.User.USER_FETCH_INFO_FAILED, e.getMessage());
        }
    }

    @Override
    public SignUpResponse saveUser(SignUpRequest user) {
        ResponseVM<SignUpResponse> response = userClient.saveUser(user);
        return response.getData();
    }
}
