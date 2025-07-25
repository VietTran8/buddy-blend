package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@FeignClient(name = "${service.group-service.name}", configuration = FeignConfig.class, path = Constants.API_PREFIX + Constants.API_SUB_PREFIX_GROUP)
public interface GroupClient {

    @GetMapping("/min/{groupId}")
    ResponseVM<GroupDTO> getGroupInfoById(@PathVariable("groupId") String groupId);

    @GetMapping("")
    ResponseVM<List<GroupDTO>> getMyGroups();

    @GetMapping("/{groupId}/allow-fetch-post")
    ResponseVM<Boolean> allowFetchPost(@PathVariable("groupId") String groupId);

    @GetMapping("/{groupId}/members/id")
    ResponseVM<List<String>> getMemberIdList(@PathVariable("groupId") String groupId);
}
