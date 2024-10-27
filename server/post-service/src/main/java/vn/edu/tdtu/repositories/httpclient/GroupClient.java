package vn.edu.tdtu.repositories.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.response.GroupInfo;

import java.util.List;

@FeignClient(name = "${service.group-service.name}", configuration = FeignConfig.class)
public interface GroupClient {
    @GetMapping("/api/v1/groups/{groupId}/min")
    public ResDTO<GroupInfo> getGroupInfoById(@PathVariable("groupId") String groupId);

    @GetMapping("/api/v1/groups")
    public ResDTO<List<GroupInfo>> getMyGroups(@RequestHeader("Authorization") String accessToken);
}
