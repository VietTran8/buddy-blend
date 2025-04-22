package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.model.es.SyncUser;

import java.util.List;

public interface UserService {
    void saveUser(SyncUser user);

    void updateUser(SyncUser user);

    void deleteUser(SyncUser user);

    List<User> searchUserFullName(String accessToken, String name, String fuzziness);
}
