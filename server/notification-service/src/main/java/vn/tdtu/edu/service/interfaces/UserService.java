package vn.tdtu.edu.service.interfaces;

import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    public UserDTO findById(String userId);

    public List<UserDTO> findByIds(List<String> ids);
}
