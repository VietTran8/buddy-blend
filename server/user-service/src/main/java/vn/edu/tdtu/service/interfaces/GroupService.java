package vn.edu.tdtu.service.interfaces;

import java.util.List;

public interface GroupService {
    public List<String> getFriendUserIdsInGroup(String accessToken, String groupId);
}
