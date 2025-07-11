package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.PromoteToAdminRequest;
import vn.edu.tdtu.dto.response.PromoteToAdminResponse;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

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
        if (!currentUserIsGroupAdmin(groupId))
            throw new BadRequestException(MessageCode.Group.GROUP_NOT_PERMITTED);
    }

    @Override
    public ResponseVM<?> promoteToAdmin(PromoteToAdminRequest request) {
        adminCheck(request.getGroupId());

        GroupMember foundMember = groupMemberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_MEMBER_NOT_FOUND));

        foundMember.setAdmin(!foundMember.isAdmin());

        groupMemberRepository.save(foundMember);

        ResponseVM<PromoteToAdminResponse> response = new ResponseVM<>();
        response.setMessage(foundMember.isAdmin() ? MessageCode.Group.GROUP_MEMBER_PROMOTED : MessageCode.Group.GROUP_MEMBER_REVOKED);
        response.setData(new PromoteToAdminResponse(request.getGroupId(), foundMember.isAdmin()));
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }
}
