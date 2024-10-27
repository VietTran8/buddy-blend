package vn.edu.tdtu.service;

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

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void removeGroupMemberById(String id) {
        groupMemberRepository.deleteMemberById(id);
    }

    @Transactional
    public ResDTO<?> removeMember(String groupId, String memberId) {
        Group foundGroup = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        GroupService.adminCheck(foundGroup);

        removeGroupMemberById(memberId);

        return new ResDTO<>(
                Message.GROUP_MEMBER_DELETED_MSG,
                null,
                HttpServletResponse.SC_OK
        );
    }
}
