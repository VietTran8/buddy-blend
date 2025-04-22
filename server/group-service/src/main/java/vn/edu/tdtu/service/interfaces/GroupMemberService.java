package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;

public interface GroupMemberService {
    public void removeGroupMemberById(String id);

    public ResDTO<?> removeMember(String groupId, String memberId);
}
