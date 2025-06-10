package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.GroupDTO;

import java.util.List;

public interface GroupService {
    public GroupDTO getGroupById(String accessToken, String groupId);

    public List<GroupDTO> getMyGroups(String accessToken);

    public boolean allowFetchPost(String accessToken, String groupId);

    public List<String> getMemberIdList(String accessToken, String groupId);
}
