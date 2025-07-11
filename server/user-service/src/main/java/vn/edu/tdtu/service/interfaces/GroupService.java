package vn.edu.tdtu.service.interfaces;

import java.util.List;

public interface GroupService {
    List<String> getFriendUserIdsInGroup(String accessToken, String groupId);
}
