package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.repository.httpclient.GroupClient;
import vn.edu.tdtu.service.interfaces.GroupService;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupClient groupClient;

    @Override
    public List<String> getFriendUserIdsInGroup(String accessToken, String groupId) {
        ResponseVM<List<String>> response = groupClient.getFriendUserIdsInGroup(accessToken, groupId);

        return response.getData();
    }
}
