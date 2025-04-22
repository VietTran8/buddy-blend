package vn.tdtu.edu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.FindByIdsRequest;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.model.data.User;
import vn.tdtu.edu.repository.httpclient.UserClient;
import vn.tdtu.edu.service.interfaces.UserService;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserClient userClient;

    @Override
    public User findById(String userId) {
        if (userId == null)
            return null;

        ResDTO<User> response = userClient.findById(userId);

        log.info(response.toString());

        return response.getData();
    }

    @Override
    public List<User> findByIds(List<String> ids) {
        ResDTO<List<User>> response = userClient.findByIds(new FindByIdsRequest(ids
                .stream()
                .filter(Objects::nonNull)
                .toList()
        ));

        log.info(response.toString());

        return response.getData();
    }
}