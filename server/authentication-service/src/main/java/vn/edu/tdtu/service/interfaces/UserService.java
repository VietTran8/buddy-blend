package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.SignUpRequest;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.model.User;

public interface UserService {
    public User getUserInfo(String email);
    public SignUpResponse saveUser(SignUpRequest user);
}
