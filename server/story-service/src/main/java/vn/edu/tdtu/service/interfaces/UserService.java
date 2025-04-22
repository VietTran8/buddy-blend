package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.User;

import java.util.List;

public interface UserService {
    public List<User> getUsersByIds(String accessToken, List<String> userIds);

    public User getUserById(String accessToken, String userId);

    public List<User> getUserFriends(String accessToken);
}
