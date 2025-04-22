package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.User;

import java.util.List;

public interface UserService {
    public User findById(String accessToken, String userId);

    public List<User> findByIds(String accessToken, List<String> ids);
}
