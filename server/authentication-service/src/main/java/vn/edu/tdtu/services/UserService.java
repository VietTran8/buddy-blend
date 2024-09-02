package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.SignUpRequest;
import vn.edu.tdtu.dtos.response.SignUpResponse;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repository.UserClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
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
