package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.edu.tdtu.config.openfeign.FeignConfig;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.GroupInfo;

import java.util.List;

@FeignClient(name = "${service.group-service.name}", configuration = FeignConfig.class, path = "/api/v1/groups")
public interface GroupClient {

    @GetMapping("/min/{groupId}")
    public ResDTO<GroupInfo> getGroupInfoById(@RequestHeader("Authorization") String accessToken, @PathVariable("groupId") String groupId);

    @GetMapping("")
    public ResDTO<List<GroupInfo>> getMyGroups(@RequestHeader("Authorization") String accessToken);

    @GetMapping("/{groupId}/allow-fetch-post")
    public ResDTO<Boolean> allowFetchPost(@RequestHeader("Authorization") String accessToken, @PathVariable("groupId") String groupId);

    @GetMapping("/{groupId}/members/id")
    public ResDTO<List<String>> getMemberIdList(@RequestHeader("Authorization") String accessToken, @PathVariable("groupId") String groupId);
}
