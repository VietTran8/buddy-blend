package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.repository.GroupRepository;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.service.interfaces.GroupMemberService;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final GroupAdminService groupAdminService;

    @Override
    @Transactional
    public void removeGroupMemberById(String id) {
        groupMemberRepository.deleteMemberById(id);
    }

    @Override
    @Transactional
    public ResponseVM<?> removeMember(String groupId, String memberId) {
        groupAdminService.adminCheck(groupId);

        removeGroupMemberById(memberId);

        return new ResponseVM<>(
                MessageCode.Group.GROUP_MEMBER_DELETED,
                null,
                HttpServletResponse.SC_OK
        );
    }
}
