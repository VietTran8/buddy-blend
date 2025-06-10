package vn.tdtu.edu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.edu.dto.FindByIdsRequest;
import vn.tdtu.edu.dto.ResDTO;
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
    public UserDTO findById(String userId) {
        if (userId == null)
            return null;

        ResDTO<UserDTO> response = userClient.findById(userId);

        log.info(response.toString());

        return response.getData();
    }

    @Override
    public List<UserDTO> findByIds(List<String> ids) {
        ResDTO<List<UserDTO>> response = userClient.findByIds(new FindByIdsRequest(ids
                .stream()
                .filter(Objects::nonNull)
                .toList()
        ));

        log.info(response.toString());

        return response.getData();
    }
}