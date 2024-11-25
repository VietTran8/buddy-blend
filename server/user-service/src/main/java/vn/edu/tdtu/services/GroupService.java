package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.repositories.httpclient.GroupClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupClient groupClient;

    public List<String> getFriendUserIdsInGroup(String accessToken, String groupId) {
        ResDTO<List<String>> response = groupClient.getFriendUserIdsInGroup(accessToken, groupId);

        return response.getData();
    }
}
