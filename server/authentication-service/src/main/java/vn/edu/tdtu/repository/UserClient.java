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

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {

    @GetMapping("/{email}/for-auth")
    public ResDTO<User> getUserInfo(@PathVariable("email") String email);

    @PostMapping("/save")
    public ResDTO<SignUpResponse> saveUser(@RequestBody SignUpRequest requestBody);
}
