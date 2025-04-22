package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.repository.GroupRepository;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.service.interfaces.GroupMemberService;

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
    public ResDTO<?> removeMember(String groupId, String memberId) {
        groupAdminService.adminCheck(groupId);

        removeGroupMemberById(memberId);

        return new ResDTO<>(
                MessageCode.GROUP_MEMBER_DELETED,
                null,
                HttpServletResponse.SC_OK
        );
    }
}
