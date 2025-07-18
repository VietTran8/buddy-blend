package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.es.SyncUser;
import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(SyncUser user);

    void updateUser(SyncUser user);

    void deleteUser(SyncUser user);

    List<UserDTO> searchUserFullName(String name, String fuzziness);
}
