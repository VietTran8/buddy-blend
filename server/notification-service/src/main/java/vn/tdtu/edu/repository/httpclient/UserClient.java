package vn.tdtu.edu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.edu.config.openfeign.FeignConfig;
import vn.tdtu.edu.dto.FindByIdsRequest;
import vn.tdtu.edu.dto.ResDTO;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {

    @GetMapping("/{userId}")
    ResDTO<UserDTO> findById(@PathVariable("userId") String userId);

    @GetMapping("/by-ids")
    ResDTO<List<UserDTO>> findByIds(@RequestBody FindByIdsRequest request);
}
