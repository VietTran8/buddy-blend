package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public GroupInfo getGroupById(String groupId) {
        ResDTO<GroupInfo> response = groupClient.getGroupInfoById(groupId);
        log.info("getGroupById: " + response.toString());

        return response.getData();
    }

    public List<GroupInfo> getMyGroups(String accessToken) {
        ResDTO<List<GroupInfo>> response = groupClient.getMyGroups(accessToken);
        log.info("getMyGroups: " + response.toString());

        return response.getData();
    }
}
