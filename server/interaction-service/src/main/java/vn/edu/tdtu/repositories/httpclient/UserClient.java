package vn.edu.tdtu.repositories.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.requests.user.FindByIdsRequest;
import vn.edu.tdtu.models.User;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/api/v1/users/{userId}")
    public ResDTO<User> findById(@RequestHeader("Authorization") String accessToken, @PathVariable("userId") String userId);

    @GetMapping("/api/v1/users/{userId}")
    public ResDTO<User> findById(@PathVariable("userId") String userId);

    @GetMapping("/api/v1/users/{email}/for-auth")
    public ResDTO<User> getUserInfo(@PathVariable("email") String email);

    @GetMapping("/api/v1/users/by-ids")
    public ResDTO<List<User>> findByIds(@RequestHeader("Authorization") String accessToken, @RequestBody FindByIdsRequest request);
}