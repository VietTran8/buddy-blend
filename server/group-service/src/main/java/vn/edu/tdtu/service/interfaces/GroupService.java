package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.GroupIdResponse;
import vn.edu.tdtu.enums.EGetMemberOption;
import vn.edu.tdtu.enums.EHandleLeaveType;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

public interface GroupService {
    ResponseVM<GroupIdResponse> createGroup(CreateGroupRequest payload);

    ResponseVM<?> updateGroupInfo(String id, UpdateGroupRequest payload);

    ResponseVM<?> deleteGroup(String id);

    ResponseVM<?> joinGroup(String groupId);

    ResponseVM<?> handleCancelPendingAndLeaveGroup(LeaveGroupRequest payload, EHandleLeaveType type);

    ResponseVM<?> getMyGroups();

    ResponseVM<?> getGroupById(String accessToken, String groupId);

    ResponseVM<?> getAllGroupByIds(List<String> groupId);

    ResponseVM<?> getGroupByIdForPost(String groupId);

    ResponseVM<?> moderateMember(ModerateMemberRequest payload);

    void inviteUsers(String accessToken, InviteUsersRequest payload);

    ResponseVM<?> getPendingMembersList(String accessToken, String groupId);

    ResponseVM<?> getGroupMembers(String accessToken, String groupId, int page, int size, EGetMemberOption option);

    ResponseVM<?> getGroupMembers(String groupId);

    ResponseVM<?> getAllFriendGroupMemberUserIds(String accessToken, String groupId);

    ResponseVM<?> isPrivateGroupOrUserJoined(String groupId);
}