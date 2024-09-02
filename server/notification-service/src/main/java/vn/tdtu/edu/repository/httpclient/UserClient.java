package vn.tdtu.edu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vn.tdtu.edu.config.openfeign.FeignConfig;
import vn.tdtu.edu.dtos.FindByIdsRequest;
import vn.tdtu.edu.dtos.ResDTO;
import vn.tdtu.edu.model.User;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/v1/users/{userId}")
    public ResDTO<User> findById(@PathVariable("userId") String userId);

    @GetMapping("/api/v1/users/by-ids")
    public ResDTO<List<User>> findByIds(@RequestBody FindByIdsRequest request);
}
