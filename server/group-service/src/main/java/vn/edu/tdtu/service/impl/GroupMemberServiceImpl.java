package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.repository.GroupRepository;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.service.interfaces.GroupMemberService;
import vn.edu.tdtu.service.interfaces.GroupService;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final GroupAdminService groupAdminService;

    @Transactional
    public void removeGroupMemberById(String id) {
        groupMemberRepository.deleteMemberById(id);
    }

    @Transactional
    public ResDTO<?> removeMember(String groupId, String memberId) {
        groupAdminService.adminCheck(groupId);

        removeGroupMemberById(memberId);

        return new ResDTO<>(
                Message.GROUP_MEMBER_DELETED_MSG,
                null,
                HttpServletResponse.SC_OK
        );
    }
}
