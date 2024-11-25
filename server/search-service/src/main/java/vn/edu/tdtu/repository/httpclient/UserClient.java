package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.FindByUserIdsReq;
import vn.edu.tdtu.model.User;

import java.util.List;

@FeignClient(name = "${service.user-service.name}", configuration = FeignConfig.class, path = "/api/v1/users")
public interface UserClient {

    @PostMapping("/by-ids")
    public ResDTO<List<User>> findByIds(@RequestHeader("Authorization") String accessToken, @RequestBody FindByUserIdsReq request);
}