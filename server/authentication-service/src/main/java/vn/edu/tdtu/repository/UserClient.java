package vn.edu.tdtu.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.tdtu.config.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.SignUpRequest;
import vn.edu.tdtu.dtos.response.SignUpResponse;
import vn.edu.tdtu.models.User;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/v1/users/{email}/for-auth")
    public ResDTO<User> getUserInfo(@PathVariable("email") String email);

    @PostMapping("/api/v1/users/save")
    public ResDTO<SignUpResponse> saveUser(@RequestBody SignUpRequest requestBody);
}
