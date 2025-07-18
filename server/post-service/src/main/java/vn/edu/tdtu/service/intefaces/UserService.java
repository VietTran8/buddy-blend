package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO findById(String userId);

    List<UserDTO> findByIds(List<String> ids);

    List<UserDTO> findUserFriendIds();

    List<String> findUserFriendIdsByUserId(String userId);
}
