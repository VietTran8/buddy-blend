package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.openfeign.FeignConfig;

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
