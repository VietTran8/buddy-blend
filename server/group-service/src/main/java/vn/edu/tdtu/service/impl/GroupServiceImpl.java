package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.*;
import vn.edu.tdtu.enums.*;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.mapper.GroupMapper;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.model.Member;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.publisher.InviteUserPublisher;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.repository.GroupRepository;
import vn.edu.tdtu.repository.MemberRepository;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.GroupMemberService;
import vn.edu.tdtu.service.interfaces.GroupService;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberService groupMemberService;
    private final GroupMapper groupMapper;
    private final UserClient userClient;
    private final InviteUserPublisher kafkaPublisher;

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
        ResDTO<JoinGroupResponse> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);

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

        response.setData(new JoinGroupResponse(groupId, group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE) ? EJoinGroupStatus.PENDING : EJoinGroupStatus.SUCCESS));

        return response;
    }

    @Transactional
    public ResDTO<?> handleCancelPendingAndLeaveGroup(LeaveGroupRequest payload, EHandleLeaveType type){
        String userId = SecurityContextUtils.getUserId();

        GroupMember foundMember = type.equals(EHandleLeaveType.CANCEL_PENDING) ?  groupMemberRepository.findPendingMemberInGroup(payload.getGroupId(), payload.getMemberId())
                .orElseThrow(() -> new BadRequestException(Message.GROUP_MEMBER_NOT_FOUND_MSG)) :
                groupMemberRepository.findByGroupIdAndMemberId(payload.getGroupId(), payload.getMemberId())
                .orElseThrow(() -> new BadRequestException(Message.GROUP_MEMBER_NOT_FOUND_MSG));

        if(!foundMember.getMember().getUserId().equals(userId))
            throw new BadRequestException(Message.GROUP_NOT_PERMITTED_MSG);

        groupMemberService.removeGroupMemberById(foundMember.getId());

        ResDTO<?> response = new ResDTO<>();

        response.setData(null);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(type.equals(EHandleLeaveType.CANCEL_PENDING) ?
                Message.GROUP_PENDING_CANCELLED_MSG :
                Message.GROUP_LEAVED_MSG);

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

        group.setPrivate(group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE));

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

    public void inviteUsers(String accessToken, InviteUsersRequest payload){
        Notification notification = new Notification();
        String userId = SecurityContextUtils.getUserId();

        User foundUser = userClient.findById(accessToken, userId).getData();

        if(foundUser == null)
            throw new UnauthorizedException("You are not authenticated");

        Group foundGroup = groupRepository.findByIdAndIsDeleted(payload.getGroupId(), false)
                .orElseThrow(() -> new BadRequestException(Message.GROUP_NOT_FOUND_MSG));

        notification.setAvatarUrl(foundUser.getProfilePicture());
        notification.setUserFullName(String.join(" ", foundUser.getFirstName(), foundUser.getMiddleName(), foundUser.getLastName()));
        notification.setContent(notification.getUserFullName() + " mời bạn tham gia nhóm " + foundGroup.getName());
        notification.setRefId(foundGroup.getId());
        notification.setTitle("Lời mời tham gia nhóm");
        notification.setFromUserId(userId);
        notification.setToUserIds(payload.getUserIds());
        notification.setType(ENotificationType.INVITE_USERS);
        notification.setCreateAt(new Date());

        kafkaPublisher.publishInviteUsers(notification);
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

        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        response.setData(
                pendingMembers.stream()
                        .map(member -> new GroupMemberResponse(member, userMap.get(member.getMember().getUserId())))
                        .toList()
        );

        return response;
    }

    public ResDTO<?> getGroupMembers(String accessToken, String groupId, int page, int size, EGetMemberOption option) {
        ResDTO<PaginationResponse<GroupMemberResponse>> response = new ResDTO<>();
        Page<GroupMember> memberPage;

        switch (option) {
            case ADMIN_MEMBERS -> {
                memberPage = groupMemberRepository.findAdminMembersByGroupId(groupId, PageRequest.of(page - 1, size));
                break;
            }
            case NEW_MEMBERS -> {
                LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
                memberPage = groupMemberRepository.findNewMembersByGroupId(groupId, threeDaysAgo, PageRequest.of(page - 1, size));
                break;
            }
            case FRIEND_MEMBERS -> {
                List<User> userFriends = userClient.findUserFriendIdsByUserToken(accessToken).getData();

                memberPage = groupMemberRepository.findFriendMembersByGroupId(groupId, userFriends.stream().map(User::getId).toList(), PageRequest.of(page - 1, size));
                break;
            }
            default -> {
                memberPage = groupMemberRepository.findAllMembersByGroupId(groupId, PageRequest.of(page - 1, size));
                break;
            }
        }

        List<GroupMember> memberList = memberPage.get().toList();

        List<String> userIds = memberList.stream().map(member -> member.getMember().getUserId()).toList();
        List<User> users = userClient.findByIds(accessToken, new FindByIdsRequest(userIds)).getData();
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        response.setData(new PaginationResponse<>(
                page,
                size,
                memberPage.getTotalPages(),
                (int) memberPage.getTotalElements(),
                memberPage.get().map(member -> new GroupMemberResponse(member, userMap.get(member.getMember().getUserId()))).toList()
        ));
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(Message.GROUP_MEMBER_FETCHED_SUCCESS_MSG);

        return response;
    }

    public ResDTO<?> getAllFriendGroupMemberUserIds(String accessToken, String groupId) {
        ResDTO<List<String>> response = new ResDTO<>();

        List<User> userFriends = userClient.findUserFriendIdsByUserToken(accessToken).getData();
        List<GroupMember> memberList = groupMemberRepository.findFriendMembersByGroupId(groupId, userFriends.stream().map(User::getId).toList());

        List<String> userIds = memberList.stream().map(member -> member.getMember().getUserId()).toList();

        response.setData(userIds);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(Message.GROUP_MEMBER_FETCHED_SUCCESS_MSG);

        return response;
    }

    public ResDTO<?> isPrivateGroupOrUserJoined(String groupId) {
        String userId = SecurityContextUtils.getUserId();

        if(groupRepository.getIsJoinedToGroup(groupId, userId))
            return new ResDTO<>(
                    Message.GROUP_MEMBER_JOINED_MSG,
                    true,
                    HttpServletResponse.SC_OK
            );

        Group foundGroup = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new NotFoundException(Message.GROUP_NOT_FOUND_MSG));

        if(foundGroup.getPrivacy().equals(EGroupPrivacy.PRIVACY_PUBLIC))
            return new ResDTO<>(
                    Message.GROUP_IS_PUBLIC_MSG,
                    true,
                    HttpServletResponse.SC_OK
            );

        return new ResDTO<>(
                Message.GROUP_IS_PRIVATE_MSG,
                false,
                HttpServletResponse.SC_OK
        );
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
