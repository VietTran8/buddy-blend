package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.response.GroupInfo;
import vn.edu.tdtu.repositories.httpclient.GroupClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final GroupClient groupClient;

    @Cacheable(key = "T(java.util.Objects).hash(#a0, #a1)", value = "single-group", unless = "#result == null")
    public GroupInfo getGroupById(String accessToken, String groupId) {
        ResDTO<GroupInfo> response = groupClient.getGroupInfoById(accessToken, groupId);
        log.info("getGroupById: " + response.toString());

        return response.getData();
    }

    public List<GroupInfo> getMyGroups(String accessToken) {
        ResDTO<List<GroupInfo>> response = groupClient.getMyGroups(accessToken);
        log.info("getMyGroups: " + response.toString());

        return response.getData();
    }

    public boolean allowFetchPost(String accessToken, String groupId) {
        ResDTO<Boolean> allowResponse = groupClient.allowFetchPost(accessToken, groupId);
        log.info("getAllowFetch: " + allowResponse.toString());

        return allowResponse.getData();
    }
}
