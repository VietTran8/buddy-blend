package vn.edu.tdtu.repository.http;

import org.springframework.cloud.openfeign.FeignClient;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.model.data.User;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {
    @GetMapping("/{userId}")
    public ResDTO<User> findById(@RequestHeader("Authorization") String accessToken, @PathVariable("userId") String userId);

    @GetMapping("/by-ids")
    public ResDTO<List<User>> findByIds(@RequestHeader("Authorization") String accessToken, @RequestBody FindByIdsRequest request);

    @GetMapping("/friends")
    public ResDTO<List<User>> findUserFriendIdsByUserToken(@RequestHeader("Authorization") String accessToken);
}
