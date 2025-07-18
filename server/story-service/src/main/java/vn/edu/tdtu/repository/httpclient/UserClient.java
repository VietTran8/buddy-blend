package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {
    @GetMapping("/by-id/{userId}")
    ResponseVM<UserDTO> findById(@PathVariable("userId") String userId);

    @GetMapping("/by-ids")
    ResponseVM<List<UserDTO>> findByIds(@RequestBody FindByIdsRequest request);

    @GetMapping("/friends")
    ResponseVM<List<UserDTO>> findUserFriendIdsByUserToken();
}
