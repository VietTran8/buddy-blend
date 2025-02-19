package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.repository.httpclient.GroupClient;
import vn.edu.tdtu.service.interfaces.GroupService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupClient groupClient;

    public List<String> getFriendUserIdsInGroup(String accessToken, String groupId) {
        ResDTO<List<String>> response = groupClient.getFriendUserIdsInGroup(accessToken, groupId);

        return response.getData();
    }
}
