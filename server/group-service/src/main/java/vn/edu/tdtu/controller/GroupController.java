package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.enums.EGetMemberOption;
import vn.edu.tdtu.enums.EHandleLeaveType;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.service.interfaces.GroupMemberService;
import vn.edu.tdtu.service.interfaces.GroupService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_GROUP)
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupAdminService groupAdminService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllGroupByIds(@RequestParam("ids") List<String> requestParam) {
        ResponseVM<?> response = groupService.getAllGroupByIds(requestParam);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.getGroupById(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/min/{groupId}")
    public ResponseEntity<?> getGroupByIdForPost(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.getGroupByIdForPost(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getMyGroups() {
        ResponseVM<?> response = groupService.getMyGroups();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getAllMembersList(@PathVariable("groupId") String groupId, 
                                               @RequestParam(name = "page", required = false, defaultValue = "1") int page, 
                                               @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        ResponseVM<?> response = groupService.getGroupMembers(groupId, page, size, EGetMemberOption.ALL_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/id")
    public ResponseEntity<?> getAllMemberIdsList(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.getGroupMembers(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/new")
    public ResponseEntity<?> getNewMembersList(
            @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResponseVM<?> response = groupService.getGroupMembers(groupId, page, size, EGetMemberOption.NEW_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/admin")
    public ResponseEntity<?> getAdminMembersList(
            @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResponseVM<?> response = groupService.getGroupMembers(groupId, page, size, EGetMemberOption.ADMIN_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/friend")
    public ResponseEntity<?> getFriendMembersList(
            @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResponseVM<?> response = groupService.getGroupMembers(groupId, page, size, EGetMemberOption.FRIEND_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/friend/all")
    public ResponseEntity<?> getFriendMembersList(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.getAllFriendGroupMemberUserIds(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/pending")
    public ResponseEntity<?> getPendingMembersList(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.getPendingMembersList(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/allow-fetch-post")
    public ResponseEntity<?> getAllowFetchPost(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.isPrivateGroupOrUserJoined(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping()
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest payload) {
        ResponseVM<?> response = groupService.createGroup(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/join/{groupId}")
    public ResponseEntity<?> joinGroup(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.joinGroup(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/invite")
    public ResponseEntity<?> inviteUsers(@RequestBody InviteUsersRequest payload) {
        groupService.inviteUsers(payload);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/member/moderate")
    public ResponseEntity<?> moderateMember(@RequestBody ModerateMemberRequest payload) {
        ResponseVM<?> response = groupService.moderateMember(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/leave")
    public ResponseEntity<?> leaveGroup(@RequestBody LeaveGroupRequest payload) {
        ResponseVM<?> response = groupService.handleCancelPendingAndLeaveGroup(payload, EHandleLeaveType.LEAVE);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/promoteToAdmin")
    public ResponseEntity<?> promoteToAdmin(@RequestBody PromoteToAdminRequest payload) {
        ResponseVM<?> response = groupAdminService.promoteToAdmin(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/cancel-pending")
    public ResponseEntity<?> cancelPendingGroup(@RequestBody LeaveGroupRequest payload) {
        ResponseVM<?> response = groupService.handleCancelPendingAndLeaveGroup(payload, EHandleLeaveType.CANCEL_PENDING);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{groupId}/member/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") String memberId, @PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupMemberService.removeMember(groupId, memberId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable("groupId") String groupId, @RequestBody UpdateGroupRequest payload) {
        ResponseVM<?> response = groupService.updateGroupInfo(groupId, payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> delete(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = groupService.deleteGroup(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
