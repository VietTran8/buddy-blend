package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@FeignClient(name = "${service.group-service.name}", configuration = FeignConfig.class, path = "/api/v1/groups")
public interface GroupClient {
    @GetMapping("/{groupId}/members/friend/all")
    ResponseVM<List<String>> getFriendUserIdsInGroup(@PathVariable("groupId") String groupId);
}
