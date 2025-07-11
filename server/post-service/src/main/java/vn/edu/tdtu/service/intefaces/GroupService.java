package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.GroupDTO;

import java.util.List;

public interface GroupService {
    GroupDTO getGroupById(String accessToken, String groupId);

    List<GroupDTO> getMyGroups(String accessToken);

    boolean allowFetchPost(String accessToken, String groupId);

    List<String> getMemberIdList(String accessToken, String groupId);
}
