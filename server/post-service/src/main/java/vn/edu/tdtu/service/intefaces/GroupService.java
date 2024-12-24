package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.response.GroupInfo;

import java.util.List;

public interface GroupService {
    public GroupInfo getGroupById(String accessToken, String groupId);
    public List<GroupInfo> getMyGroups(String accessToken);
    public boolean allowFetchPost(String accessToken, String groupId);
}
