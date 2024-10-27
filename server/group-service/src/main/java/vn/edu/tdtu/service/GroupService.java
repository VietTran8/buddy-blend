package vn.edu.tdtu.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateGroupRequest;
import vn.edu.tdtu.dto.request.FindByIdsRequest;
import vn.edu.tdtu.dto.request.ModerateMemberRequest;
import vn.edu.tdtu.dto.request.UpdateGroupRequest;
import vn.edu.tdtu.dto.response.GroupIdResponse;
import vn.edu.tdtu.dto.response.GroupMemberResponse;
import vn.edu.tdtu.dto.response.GroupResponse;
import vn.edu.tdtu.enums.EGroupPrivacy;
import vn.edu.tdtu.enums.EMemberAcceptation;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.mapper.GroupMapper;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.model.Member;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.repository.GroupRepository;
import vn.edu.tdtu.repository.MemberRepository;
import vn.edu.tdtu.repository.http.UserClient;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;

    private final GroupMemberService groupMemberService;

    private final GroupMapper groupMapper;
    private final UserClient userClient;

    public ResDTO<GroupIdResponse> createGroup(CreateGroupRequest payload) {
        String userId = SecurityContextUtils.getUserId();

        Group group = new Group();

        Member member = memberRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setUserId(userId);

                    return memberRepository.save(newMember);
                });

        GroupMember newGroupMember = new GroupMember();
        newGroupMember.setGroup(group);
        newGroupMember.setAdmin(true);
        newGroupMember.setPending(false);
        newGroupMember.setMember(member);
        newGroupMember.setJoinedAt(LocalDateTime.now());

        group.setName(payload.getName());
        group.setCreatedAt(LocalDateTime.now());
        group.setPrivacy(payload.getPrivacy());
        group.setDescription(payload.getDescription());
        group.setCreatedBy(userId);
        group.setGroupMembers(List.of(newGroupMember));

        groupRepository.save(group);

        return new ResDTO<>(
                Message.GROUP_CREATED_SUCCESS_MSG,
                new GroupIdResponse(group.getId()),
                HttpServletResponse.SC_CREATED
        );
    }

    public ResDTO<?> updateGroupInfo(String id, UpdateGroupRequest payload) {
        ResDTO<GroupIdResponse> response = new ResDTO<GroupIdResponse>(
                Message.GROUP_UPDATED_SUCCESS_MSG,
                new GroupIdResponse(id),
                HttpServletResponse.SC_OK
        );

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        adminCheck(group);

        if(payload.getName() != null)
            group.setName(payload.getName());

        if(payload.getAvatar() != null)
            group.setAvatar(payload.getAvatar());

        if(payload.getDescription() != null)
            group.setDescription(payload.getDescription());

        if(payload.getCover() != null)
            group.setCover(payload.getCover());

        groupRepository.save(group);

        return response;
    }

    public ResDTO<?> deleteGroup(String id) {
        ResDTO<GroupIdResponse> response = new ResDTO<GroupIdResponse>(
                Message.GROUP_DELETED_SUCCESS_MSG,
                new GroupIdResponse(id),
                HttpServletResponse.SC_OK
        );

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                        .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        adminCheck(group);

        group.setDeleted(true);
        groupRepository.save(group);

        return response;
    }

    public ResDTO<?> joinGroup(String groupId) {
        ResDTO<GroupIdResponse> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(new GroupIdResponse(groupId));

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false).
                orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        String userId = SecurityContextUtils.getUserId();

        if(groupRepository.getIsJoinedToGroup(groupId, userId))
            throw new BadRequestException(Message.GROUP_ALREADY_JOINED_MSG);

        response.setMessage(
                group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE) ?
                        Message.GROUP_JOIN_PENDING_MSG:
                        Message.GROUP_JOINED_MSG
        );

        Optional<Member> optionalMember = memberRepository.findByUserId(userId);

        Member foundMember = optionalMember.orElseGet(() -> {
            Member newMember = new Member(null, userId, null);
            return memberRepository.save(newMember);
        });

        GroupMember newGroupMember = new GroupMember();
        newGroupMember.setGroup(group);
        newGroupMember.setAdmin(false);
        newGroupMember.setJoinedAt(LocalDateTime.now());
        newGroupMember.setPending(group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE));
        newGroupMember.setMember(foundMember);

        group.getGroupMembers().add(newGroupMember);

        groupRepository.save(group);

        return response;
    }

    public ResDTO<?> getMyGroups() {
        return new ResDTO<>(
                Message.GROUP_FETCHED_MSG,
                groupRepository.findJoinedGroups(SecurityContextUtils.getUserId()),
                HttpServletResponse.SC_OK
        );
    }

    public ResDTO<?> getGroupById(String accessToken, String groupId) {
        ResDTO<GroupResponse> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(Message.GROUP_FETCHED_MSG);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        GroupResponse groupResponse = groupMapper.mapToDto(accessToken, group);

        response.setData(groupResponse);

        return response;
    }

    public ResDTO<?> getGroupByIdForPost(String groupId) {
        ResDTO<Group> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(Message.GROUP_FETCHED_MSG);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        response.setData(group);

        return response;
    }

    public ResDTO<?> moderateMember(ModerateMemberRequest payload){
        Group foundGroup = groupRepository.findByIdAndIsDeleted(payload.getGroupId(), false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        adminCheck(foundGroup);

        GroupMember foundMember = groupMemberRepository.findPendingMemberInGroup(payload.getGroupId(), payload.getMemberId())
                .orElseThrow(() -> new BadRequestException(Message.GROUP_MEMBER_NOT_FOUND_MSG));

        if(payload.getAcceptOption().equals(EMemberAcceptation.AGREE)) {
            foundMember.setPending(false);
            groupMemberRepository.save(foundMember);
        }
        else {
            groupMemberService.removeGroupMemberById(foundMember.getId());
        }

        return new ResDTO<>(
                payload.getAcceptOption().equals(EMemberAcceptation.AGREE) ?
                        Message.GROUP_MEMBER_ACCEPTED_MSG :
                        Message.GROUP_MEMBER_REJECTED_MSG,
                payload.getAcceptOption().equals(EMemberAcceptation.AGREE) ? foundMember.getId() : null,
                HttpServletResponse.SC_OK
        );
    }

    public ResDTO<?> getPendingMembersList(String accessToken, String groupId) {
        ResDTO<List<GroupMemberResponse>> response = new ResDTO<>();
        response.setMessage(Message.GROUP_MEMBER_FETCHED_SUCCESS_MSG);
        response.setCode(HttpServletResponse.SC_OK);

        List<GroupMember> pendingMembers = getPendingGroupMembers(groupId);

        List<String> memberUserIds = pendingMembers
                .stream()
                .map(member -> member.getMember().getUserId())
                .toList();

        List<User> users = userClient.findByIds(accessToken, new FindByIdsRequest(memberUserIds)).getData();

        response.setData(
                IntStream.range(0, pendingMembers.size())
                        .mapToObj((index) -> new GroupMemberResponse(pendingMembers.get(index), users.get(index)))
                        .toList()
        );

        return response;
    }

    private List<GroupMember> getPendingGroupMembers(String groupId) {
        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        adminCheck(group);

        return groupMemberRepository.findByGroupAndIsPending(group, true);
    }

    private static boolean currentUserIsNotGroupAdmin(Group group) {
        String userId = SecurityContextUtils.getUserId();

        return group.getGroupMembers()
                .stream()
                .noneMatch(member -> member.isAdmin() && member
                        .getMember()
                        .getUserId()
                        .equals(userId));
    }

    public static void adminCheck(Group group) {
        if(currentUserIsNotGroupAdmin(group))
            throw new UnauthorizedException(Message.GROUP_NOT_PERMITTED_MSG);
    }
}
