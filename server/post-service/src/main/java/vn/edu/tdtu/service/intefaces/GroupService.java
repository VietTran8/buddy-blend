package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.GroupDTO;

import java.util.List;

public interface GroupService {
    GroupDTO getGroupById(String groupId);

    List<GroupDTO> getMyGroups();

    boolean allowFetchPost(String groupId);

    List<String> getMemberIdList(String groupId);
}
