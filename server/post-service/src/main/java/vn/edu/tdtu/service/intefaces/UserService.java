package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO findById(String accessToken, String userId);

    List<UserDTO> findByIds(String accessToken, List<String> ids);

    List<UserDTO> findUserFriendIdsByUserToken(String token);

    List<String> findUserFriendIdsByUserId(String token, String userId);
}
