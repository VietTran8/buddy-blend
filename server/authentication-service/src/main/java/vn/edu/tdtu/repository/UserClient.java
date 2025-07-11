package vn.edu.tdtu.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.edu.tdtu.dto.request.SignUpRequest;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.model.data.User;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.viewmodel.ResponseVM;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {

    @GetMapping("/for-auth/{email}")
    ResponseVM<User> getUserInfo(@PathVariable("email") String email);

    @PostMapping("/save")
    ResponseVM<SignUpResponse> saveUser(@RequestBody SignUpRequest requestBody);
}
