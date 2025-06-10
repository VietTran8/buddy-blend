package vn.edu.tdtu.repository.httpclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.user.FindByIdsRequest;
import vn.tdtu.common.dto.UserDTO;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {
    @GetMapping("/{userId}")
    public ResDTO<UserDTO> findById(@RequestHeader("Authorization") String accessToken, @PathVariable("userId") String userId);

    @GetMapping("/by-ids")
    public ResDTO<List<UserDTO>> findByIds(@RequestHeader("Authorization") String accessToken, @RequestBody FindByIdsRequest request);

    @GetMapping("/friends")
    public ResDTO<List<UserDTO>> findUserFriendIdsByUserToken(@RequestHeader("Authorization") String accessToken);

    @GetMapping("/friends/id")
    public ResDTO<List<String>> findUserFriendIdsByUserId(@RequestHeader("Authorization") String accessToken, @RequestParam("id") String userId);
}