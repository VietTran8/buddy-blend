package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.viewmodel.ResponseVM;

public interface GroupMemberService {
    void removeGroupMemberById(String id);

    ResponseVM<?> removeMember(String groupId, String memberId);

}
