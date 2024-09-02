package vn.tdtu.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dtos.FindByIdsRequest;
import vn.tdtu.edu.dtos.ResDTO;
import vn.tdtu.edu.model.User;
import vn.tdtu.edu.repository.httpclient.UserClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    public User findById(String userId) {
        ResDTO<User> response = userClient.findById(userId);

        log.info(response.toString());

        return response.getData();
    }

    public List<User> findByIds(List<String> ids){
        ResDTO<List<User>> response = userClient.findByIds(new FindByIdsRequest(ids));

        log.info(response.toString());

        return response.getData();
    }
}