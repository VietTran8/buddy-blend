package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    public List<UserDTO> getUsersByIds(String accessToken, List<String> userIds);

    public UserDTO getUserById(String accessToken, String userId);

    public List<UserDTO> getUserFriends(String accessToken);
}
