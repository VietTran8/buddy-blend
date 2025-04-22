package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.dto.response.GroupMemberResponse;
import vn.edu.tdtu.dto.response.GroupResponse;
import vn.edu.tdtu.enums.EJoinGroupStatus;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupMapper {
    private final UserClient userClient;

    public GroupResponse mapToDto(String accessToken, Group group, boolean collectBaseInfo) {
        String userId = SecurityContextUtils.getUserId();

        List<GroupMember> first10GroupMembers = new ArrayList<>();
        if (!collectBaseInfo)
            first10GroupMembers = group.getGroupMembers()
                    .stream()
                    .filter(member -> !member.isPending())
                    .limit(10)
                    .toList();

        List<String> first10MemberIds = first10GroupMembers
                .stream()
                .map(member -> member.getMember().getUserId())
                .toList();

        List<User> first10Members = new ArrayList<>();

        if (!collectBaseInfo)
            first10Members = userClient.findByIds(accessToken, new FindByIdsRequest(first10MemberIds)).getData();

        Map<String, User> userMap = first10Members.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<GroupMemberResponse> response10MembersList = first10GroupMembers.stream()
                .map(member -> {
                    User user = userMap.remove(member.getMember().getUserId());

                    return new GroupMemberResponse(member, user);
                })
                .toList();

        List<GroupMember> groupMembers = group.getGroupMembers();

        GroupMember currentGroupMember = groupMembers.stream()
                .filter(member -> member.getMember().getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        GroupResponse response = new GroupResponse();
        response.setJoined(groupMembers
                .stream()
                .anyMatch(member -> member.getMember().getUserId().equals(userId) && !member.isPending()));

        if (!collectBaseInfo)
            response.setAdmin(groupMembers
                    .stream()
                    .anyMatch(member -> member.getMember().getUserId().equals(userId) && member.isAdmin()));

        response.setMemberCount(groupMembers.stream()
                .filter(groupMember -> !groupMember.isPending()).count());

        response.setCover(group.getCover());
        response.setId(group.getId());
        response.setAvatar(group.getAvatar());
        response.setFirstTenMembers(response10MembersList);
        response.setJoinStatus(getJoinStatus(response.isJoined(), userId, groupMembers));
        response.setCreatedAt(group.getCreatedAt());
        response.setPrivacy(group.getPrivacy());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setCurrentMemberId(currentGroupMember != null ? currentGroupMember.getId() : null);
        response.setPendingMemberCount(response.isAdmin() ? groupMembers.size() - response.getMemberCount() : null);

        return response;
    }

    private EJoinGroupStatus getJoinStatus(Boolean joined, String userId, List<GroupMember> groupMembers) {
        if (joined)
            return EJoinGroupStatus.SUCCESS;

        return groupMembers.stream().anyMatch(member -> member.isPending() && member.getMember().getUserId().equals(userId)) ?
                EJoinGroupStatus.PENDING : EJoinGroupStatus.NOT_YET;
    }
}
