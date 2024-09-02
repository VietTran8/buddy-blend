package vn.edu.tdtu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.httpclient.UserClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    public User findById(String userId) {
        ResDTO<User> response = userClient.findById(userId);
        return response.getData();
    }
}