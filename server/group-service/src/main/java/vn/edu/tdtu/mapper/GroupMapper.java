package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.dto.response.GroupMemberResponse;
import vn.edu.tdtu.dto.response.GroupResponse;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.http.UserClient;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class GroupMapper {
    private final UserClient userClient;
    public GroupResponse mapToDto(String accessToken, Group group) {
        String userId = SecurityContextUtils.getUserId();

        List<GroupMember> first10GroupMembers = group.getGroupMembers()
                .stream()
                .filter(member -> !member.isPending())
                .limit(10)
                .toList();

        List<String> first10MemberIds = first10GroupMembers
                .stream()
                .map(member -> member.getMember().getUserId())
                .toList();

        List<User> first10Members = userClient.findByIds(accessToken, new FindByIdsRequest(first10MemberIds)).getData();

        Map<String, User> userMap = first10Members.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<GroupMemberResponse> response10MembersList = first10GroupMembers.stream()
                .map(first10GroupMember -> {
                    User user = userMap.remove(first10GroupMember.getMember().getUserId());

                    return new GroupMemberResponse(first10GroupMember, user);
                })
                .toList();

        GroupResponse response = new GroupResponse();
        response.setCover(group.getCover());
        response.setId(group.getId());
        response.setAvatar(group.getAvatar());

        response.setJoined(group.getGroupMembers()
                .stream()
                .anyMatch(member -> member.getMember().getUserId().equals(userId)));

        response.setAdmin(group.getGroupMembers()
                .stream()
                .anyMatch(member -> member.getMember().getUserId().equals(userId) && member.isAdmin()));

        response.setFirstTenMembers(response10MembersList);

        response.setCreatedAt(group.getCreatedAt());
        response.setPrivacy(group.getPrivacy());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setMemberCount(group.getGroupMembers().size());

        return response;
    }

}
