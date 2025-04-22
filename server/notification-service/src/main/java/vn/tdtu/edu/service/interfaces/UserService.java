package vn.tdtu.edu.service.interfaces;

import vn.tdtu.edu.model.data.User;

import java.util.List;

public interface UserService {
    public User findById(String userId);

    public List<User> findByIds(List<String> ids);
}
