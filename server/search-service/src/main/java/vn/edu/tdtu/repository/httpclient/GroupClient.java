package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.model.data.Group;

import java.util.List;

@FeignClient(name = "${service.group-service.name}", configuration = FeignConfig.class, path = "/api/v1/groups")
public interface GroupClient {

    @GetMapping("/all")
    public ResDTO<List<Group>> getAllGroupByIds(@RequestHeader("Authorization") String tokenHeader, @RequestParam("ids") List<String> requestParam);
}
