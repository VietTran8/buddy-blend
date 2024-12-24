package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.SignUpRequest;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    public User getUserInfo(String email){
        ResDTO<User> response = userClient.getUserInfo(email);
        return response.getData();
    }

    public SignUpResponse saveUser(SignUpRequest user){
        ResDTO<SignUpResponse> response = userClient.saveUser(user);
        return response.getData();
    }
}
