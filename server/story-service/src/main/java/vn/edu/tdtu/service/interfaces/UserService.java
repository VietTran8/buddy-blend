package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsersByIds(List<String> userIds);

    UserDTO getUserById(String userId);

    List<UserDTO> getUserFriends();
}
