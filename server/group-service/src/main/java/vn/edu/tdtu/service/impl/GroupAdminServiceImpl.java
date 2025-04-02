package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.PromoteToAdminRequest;
import vn.edu.tdtu.dto.response.PromoteToAdminResponse;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.utils.SecurityContextUtils;

@RequiredArgsConstructor
@Service
public class GroupAdminServiceImpl implements GroupAdminService {
    private final GroupMemberRepository groupMemberRepository;
    private boolean currentUserIsGroupAdmin(String groupId) {
        String userId = SecurityContextUtils.getUserId();

        return groupMemberRepository.existsAdminMemberByUserIdAndGroupId(userId, groupId);
    }

    @Override
    public void adminCheck(String groupId) {
        if(!currentUserIsGroupAdmin(groupId))
            throw new BadRequestException(Message.GROUP_NOT_PERMITTED_MSG);
    }

    @Override
    public ResDTO<?> promoteToAdmin(PromoteToAdminRequest request) {
        adminCheck(request.getGroupId());

        GroupMember foundMember = groupMemberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BadRequestException(Message.GROUP_MEMBER_NOT_FOUND_MSG));

        foundMember.setAdmin(!foundMember.isAdmin());

        groupMemberRepository.save(foundMember);

        ResDTO<PromoteToAdminResponse> response = new ResDTO<>();
        response.setMessage(foundMember.isAdmin() ? Message.GROUP_MEMBER_PROMOTED_MSG : Message.GROUP_MEMBER_REVOKED_MSG);
        response.setData(new PromoteToAdminResponse(request.getGroupId(), foundMember.isAdmin()));
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }
}
