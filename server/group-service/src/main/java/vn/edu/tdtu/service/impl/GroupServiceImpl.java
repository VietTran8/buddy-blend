package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.GroupIdResponse;
import vn.edu.tdtu.dto.response.JoinGroupResponse;
import vn.edu.tdtu.dto.response.Notification;
import vn.edu.tdtu.enums.EGetMemberOption;
import vn.edu.tdtu.enums.EHandleLeaveType;
import vn.edu.tdtu.enums.EMemberAcceptation;
import vn.edu.tdtu.mapper.GroupMapper;
import vn.edu.tdtu.message.SyncGroupMsg;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.model.Member;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.GroupMemberRepository;
import vn.edu.tdtu.repository.GroupRepository;
import vn.edu.tdtu.repository.MemberRepository;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.service.interfaces.GroupMemberService;
import vn.edu.tdtu.service.interfaces.GroupService;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.dto.GroupMemberDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.group.EGroupPrivacy;
import vn.tdtu.common.enums.group.EJoinGroupStatus;
import vn.tdtu.common.enums.notification.ENotificationType;
import vn.tdtu.common.enums.search.ESyncType;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.PaginationResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

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
    private final GroupAdminService groupAdminService;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberService groupMemberService;
    private final GroupMapper groupMapper;
    private final UserClient userClient;
    private final KafkaEventPublisher kafkaPublisher;

    @Override
    public ResponseVM<GroupIdResponse> createGroup(CreateGroupRequest payload) {
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

        kafkaPublisher.publishSyncGroupData(new SyncGroupMsg(
                group.getId(),
                group.getName(),
                ESyncType.TYPE_CREATE
        ));

        return new ResponseVM<>(
                MessageCode.Group.GROUP_CREATED_SUCCESS,
                new GroupIdResponse(group.getId()),
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResponseVM<?> updateGroupInfo(String id, UpdateGroupRequest payload) {
        ResponseVM<GroupIdResponse> response = new ResponseVM<GroupIdResponse>(
                MessageCode.Group.GROUP_UPDATED_SUCCESS,
                new GroupIdResponse(id),
                HttpServletResponse.SC_OK
        );

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

        groupAdminService.adminCheck(group.getId());

        if (payload.getName() != null) {
            group.setName(payload.getName());
            kafkaPublisher.publishSyncGroupData(new SyncGroupMsg(
                    id,
                    payload.getName(),
                    ESyncType.TYPE_UPDATE
            ));
        }

        if (payload.getAvatar() != null)
            group.setAvatar(payload.getAvatar());

        if (payload.getDescription() != null)
            group.setDescription(payload.getDescription());

        if (payload.getCover() != null)
            group.setCover(payload.getCover());

        groupRepository.save(group);

        return response;
    }

    @Override
    public ResponseVM<?> deleteGroup(String id) {
        ResponseVM<GroupIdResponse> response = new ResponseVM<GroupIdResponse>(
                MessageCode.Group.GROUP_DELETED_SUCCESS,
                new GroupIdResponse(id),
                HttpServletResponse.SC_OK
        );

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

        groupAdminService.adminCheck(group.getId());

        group.setDeleted(true);
        groupRepository.save(group);

        kafkaPublisher.publishSyncGroupData(new SyncGroupMsg(
                group.getId(),
                group.getName(),
                ESyncType.TYPE_DELETE
        ));

        return response;
    }

    @Override
    public ResponseVM<?> joinGroup(String groupId) {
        ResponseVM<JoinGroupResponse> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false).
                orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

        String userId = SecurityContextUtils.getUserId();

        if (groupRepository.getIsJoinedToGroup(groupId, userId))
            throw new BadRequestException(MessageCode.Group.GROUP_ALREADY_JOINED);

        response.setMessage(
                group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE) ?
                        MessageCode.Group.GROUP_JOIN_PENDING :
                        MessageCode.Group.GROUP_JOINED
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

    @Override
    @Transactional
    public ResponseVM<?> handleCancelPendingAndLeaveGroup(LeaveGroupRequest payload, EHandleLeaveType type) {
        String userId = SecurityContextUtils.getUserId();

        GroupMember foundMember = type.equals(EHandleLeaveType.CANCEL_PENDING) ? groupMemberRepository.findPendingMemberInGroup(payload.getGroupId(), payload.getMemberId())
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_MEMBER_NOT_FOUND)) :
                groupMemberRepository.findByGroupIdAndMemberId(payload.getGroupId(), payload.getMemberId())
                        .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_MEMBER_NOT_FOUND));

        if (!foundMember.getMember().getUserId().equals(userId))
            throw new BadRequestException(MessageCode.Group.GROUP_NOT_PERMITTED);

        groupMemberService.removeGroupMemberById(foundMember.getId());

        ResponseVM<?> response = new ResponseVM<>();

        response.setData(null);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(type.equals(EHandleLeaveType.CANCEL_PENDING) ?
                MessageCode.Group.GROUP_PENDING_CANCELLED :
                MessageCode.Group.GROUP_LEAVED);

        return response;
    }

    @Override
    public ResponseVM<?> getMyGroups() {
        return new ResponseVM<>(
                MessageCode.Group.GROUP_FETCHED,
                groupRepository.findJoinedGroups(SecurityContextUtils.getUserId()),
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> getGroupById(String groupId) {
        ResponseVM<GroupDTO> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Group.GROUP_FETCHED);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

        GroupDTO groupResponse = groupMapper.mapToDto(group, false);

        response.setData(groupResponse);

        return response;
    }

    @Override
    public ResponseVM<?> getAllGroupByIds(List<String> groupIds) {
        ResponseVM<List<GroupDTO>> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Group.GROUP_FETCHED);

        List<Group> groups = groupRepository.findAllByIdInAndIsDeleted(groupIds, false);

        response.setData(groups.stream().map(
                group -> groupMapper.mapToDto(group, true)
        ).toList());

        return response;
    }

    @Override
    public ResponseVM<?> getGroupByIdForPost(String groupId) {
        ResponseVM<Group> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Group.GROUP_FETCHED);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElse(null);

        if (group != null)
            group.setPrivate(group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE));

        response.setData(group);

        return response;
    }

    @Override
    public ResponseVM<?> moderateMember(ModerateMemberRequest payload) {
        Group foundGroup = groupRepository.findByIdAndIsDeleted(payload.getGroupId(), false)
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

        groupAdminService.adminCheck(foundGroup.getId());

        GroupMember foundMember = groupMemberRepository.findPendingMemberInGroup(payload.getGroupId(), payload.getMemberId())
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_MEMBER_NOT_FOUND));

        if (payload.getAcceptOption().equals(EMemberAcceptation.AGREE)) {
            foundMember.setPending(false);
            groupMemberRepository.save(foundMember);
        } else {
            groupMemberService.removeGroupMemberById(foundMember.getId());
        }

        return new ResponseVM<>(
                payload.getAcceptOption().equals(EMemberAcceptation.AGREE) ?
                        MessageCode.Group.GROUP_MEMBER_ACCEPTED :
                        MessageCode.Group.GROUP_MEMBER_REJECTED,
                payload.getAcceptOption().equals(EMemberAcceptation.AGREE) ? foundMember.getId() : null,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public void inviteUsers(InviteUsersRequest payload) {
        Notification notification = new Notification();
        String userId = SecurityContextUtils.getUserId();

        UserDTO foundUser = userClient.findById(userId).getData();

        if (foundUser == null)
            throw new UnauthorizedException(MessageCode.Authentication.AUTH_UNAUTHORIZED);

        Group foundGroup = groupRepository.findByIdAndIsDeleted(payload.getGroupId(), false)
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

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

    @Override
    public ResponseVM<?> getPendingMembersList(String groupId) {
        ResponseVM<List<GroupMemberDTO>> response = new ResponseVM<>();
        response.setMessage(MessageCode.Group.GROUP_MEMBER_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);

        List<GroupMember> pendingMembers = getPendingGroupMembers(groupId);

        List<String> memberUserIds = pendingMembers
                .stream()
                .map(member -> member.getMember().getUserId())
                .toList();

        List<UserDTO> users = userClient.findByIds(new FindByIdsRequest(memberUserIds)).getData();

        Map<String, UserDTO> userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, user -> user));

        response.setData(
                pendingMembers.stream()
                        .map(member -> new GroupMemberDTO(
                                member.getId(),
                                member.isAdmin(),
                                member.isPending(),
                                member.getJoinedAt(),
                                userMap.get(member.getMember().getUserId()))
                        )
                        .toList()
        );

        return response;
    }

    @Override
    public ResponseVM<?> getGroupMembers(String groupId, int page, int size, EGetMemberOption option) {
        ResponseVM<PaginationResponseVM<GroupMemberDTO>> response = new ResponseVM<>();
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
                List<UserDTO> userFriends = userClient.findUserFriendIds().getData();

                memberPage = groupMemberRepository.findFriendMembersByGroupId(groupId, userFriends.stream().map(UserDTO::getId).toList(), PageRequest.of(page - 1, size));
                break;
            }
            default -> {
                memberPage = groupMemberRepository.findAllMembersByGroupId(groupId, PageRequest.of(page - 1, size));
                break;
            }
        }

        List<GroupMember> memberList = memberPage.get().toList();

        List<String> userIds = memberList.stream().map(member -> member.getMember().getUserId()).toList();
        List<UserDTO> users = userClient.findByIds(new FindByIdsRequest(userIds)).getData();
        Map<String, UserDTO> userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, user -> user));

        response.setData(new PaginationResponseVM<>(
                page,
                size,
                memberPage.getTotalPages(),
                memberPage.get().map(member -> new GroupMemberDTO(
                        member.getId(),
                        member.isAdmin(),
                        member.isPending(),
                        member.getJoinedAt(),
                        userMap.get(member.getMember().getUserId()))
                ).toList(),
                memberPage.getTotalElements()
        ));
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Group.GROUP_MEMBER_FETCHED);

        return response;
    }

    @Override
    public ResponseVM<?> getGroupMembers(String groupId) {
        List<String> memberListUserIds = groupMemberRepository.findAllMembersByGroupId(groupId)
                .stream().map(gMember -> gMember.getMember().getUserId()).toList();
        String authUserId = SecurityContextUtils.getUserId();

        if (!memberListUserIds.contains(authUserId))
            throw new BadRequestException("You are not in this group");

        ResponseVM<List<String>> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(memberListUserIds);
        response.setMessage(MessageCode.Group.GROUP_MEMBER_FETCHED);

        return response;
    }

    @Override
    public ResponseVM<?> getAllFriendGroupMemberUserIds(String groupId) {
        ResponseVM<List<String>> response = new ResponseVM<>();

        List<UserDTO> userFriends = userClient.findUserFriendIds().getData();
        List<GroupMember> memberList = groupMemberRepository.findFriendMembersByGroupId(groupId, userFriends.stream().map(UserDTO::getId).toList());

        List<String> userIds = memberList.stream().map(member -> member.getMember().getUserId()).toList();

        response.setData(userIds);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Group.GROUP_MEMBER_FETCHED);

        return response;
    }

    @Override
    public ResponseVM<?> isPrivateGroupOrUserJoined(String groupId) {
        String userId = SecurityContextUtils.getUserId();

        if (groupRepository.getIsJoinedToGroup(groupId, userId))
            return new ResponseVM<>(
                    MessageCode.Group.GROUP_MEMBER_JOINED,
                    true,
                    HttpServletResponse.SC_OK
            );

        Group foundGroup = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new NotFoundException(MessageCode.Group.GROUP_NOT_FOUND));

        if (foundGroup.getPrivacy().equals(EGroupPrivacy.PRIVACY_PUBLIC))
            return new ResponseVM<>(
                    MessageCode.Group.GROUP_IS_PUBLIC,
                    true,
                    HttpServletResponse.SC_OK
            );

        return new ResponseVM<>(
                MessageCode.Group.GROUP_IS_PRIVATE,
                false,
                HttpServletResponse.SC_OK
        );
    }

    private List<GroupMember> getPendingGroupMembers(String groupId) {
        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new BadRequestException(MessageCode.Group.GROUP_NOT_FOUND));

        groupAdminService.adminCheck(group.getId());

        return groupMemberRepository.findByGroupAndIsPending(group, true);
    }
}
