package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.repository.httpclient.GroupClient;
import vn.edu.tdtu.service.intefaces.GroupService;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final GroupClient groupClient;

    @Override
    @Cacheable(key = "T(java.util.Objects).hash(#a0, #a1)", value = "single-group", unless = "#result == null")
    public GroupDTO getGroupById(String accessToken, String groupId) {
        ResponseVM<GroupDTO> response = groupClient.getGroupInfoById(accessToken, groupId);
        log.info("getGroupById: " + response.toString());

        return response.getData();
    }

    @Override
    public List<GroupDTO> getMyGroups(String accessToken) {
        ResponseVM<List<GroupDTO>> response = groupClient.getMyGroups(accessToken);
        log.info("getMyGroups: " + response.toString());

        return response.getData();
    }

    @Override
    public boolean allowFetchPost(String accessToken, String groupId) {
        ResponseVM<Boolean> allowResponse = groupClient.allowFetchPost(accessToken, groupId);
        log.info("getAllowFetch: " + allowResponse.toString());

        return allowResponse.getData();
    }

    @Override
    public List<String> getMemberIdList(String accessToken, String groupId) {
        ResponseVM<List<String>> response = groupClient.getMemberIdList(accessToken, groupId);
        log.info("memberIdList: " + response.toString());

        return response.getData();
    }
}
