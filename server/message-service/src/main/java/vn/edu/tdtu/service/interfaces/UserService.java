package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.dto.UserDTO;

public interface UserService {
    UserDTO findById(String userId);
}
