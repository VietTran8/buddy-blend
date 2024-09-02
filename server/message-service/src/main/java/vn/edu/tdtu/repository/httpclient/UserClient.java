package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.model.User;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/v1/users/{userId}")
    public ResDTO<User> findById(@PathVariable("userId") String userId);
}
