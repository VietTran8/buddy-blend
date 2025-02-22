package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.User;

public interface UserService {
    public User findById(String userId);
}
