package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.SignUpRequest;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.model.data.User;

public interface UserService {
    User getUserInfo(String email);

    SignUpResponse saveUser(SignUpRequest user);
}
