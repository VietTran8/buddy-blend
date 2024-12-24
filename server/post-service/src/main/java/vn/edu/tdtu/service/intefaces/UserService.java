package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.model.User;

import java.util.List;

public interface UserService {
    public User findById(String accessToken, String userId);
    public List<User> findByIds(String accessToken, List<String> ids);
    public List<User> findUserFriendIdsByUserToken(String token);
}
