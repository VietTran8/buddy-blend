package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.GroupIdResponse;
import vn.edu.tdtu.enums.EGetMemberOption;
import vn.edu.tdtu.enums.EHandleLeaveType;

import java.util.List;

public interface GroupService {
    public ResDTO<GroupIdResponse> createGroup(CreateGroupRequest payload);

    public ResDTO<?> updateGroupInfo(String id, UpdateGroupRequest payload);

    public ResDTO<?> deleteGroup(String id);

    public ResDTO<?> joinGroup(String groupId);

    public ResDTO<?> handleCancelPendingAndLeaveGroup(LeaveGroupRequest payload, EHandleLeaveType type);

    public ResDTO<?> getMyGroups();

    public ResDTO<?> getGroupById(String accessToken, String groupId);

    public ResDTO<?> getAllGroupByIds(List<String> groupId);

    public ResDTO<?> getGroupByIdForPost(String groupId);

    public ResDTO<?> moderateMember(ModerateMemberRequest payload);

    public void inviteUsers(String accessToken, InviteUsersRequest payload);

    public ResDTO<?> getPendingMembersList(String accessToken, String groupId);

    public ResDTO<?> getGroupMembers(String accessToken, String groupId, int page, int size, EGetMemberOption option);

    public ResDTO<?> getGroupMembers(String groupId);

    public ResDTO<?> getAllFriendGroupMemberUserIds(String accessToken, String groupId);

    public ResDTO<?> isPrivateGroupOrUserJoined(String groupId);
}
