package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO findById(String accessToken, String userId);

    List<UserDTO> findByIds(String accessToken, List<String> ids);
}
