package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    public UserDTO findById(String accessToken, String userId);

    public List<UserDTO> findByIds(String accessToken, List<String> ids);

    public List<UserDTO> findUserFriendIdsByUserToken(String token);

    public List<String> findUserFriendIdsByUserId(String token, String userId);
}
