package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.enums.EGetMemberOption;
import vn.edu.tdtu.enums.EHandleLeaveType;
import vn.edu.tdtu.service.interfaces.GroupAdminService;
import vn.edu.tdtu.service.interfaces.GroupMemberService;
import vn.edu.tdtu.service.interfaces.GroupService;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupAdminService groupAdminService;

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@RequestHeader("Authorization") String tokenHeader, @PathVariable("groupId") String groupId){
        ResDTO<?> response = groupService.getGroupById(tokenHeader, groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/min/{groupId}")
    public ResponseEntity<?> getGroupByIdForPost(@PathVariable("groupId") String groupId){
        ResDTO<?> response = groupService.getGroupByIdForPost(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getMyGroups() {
        ResDTO<?> response = groupService.getMyGroups();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getAllMembersList(
            @RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResDTO<?> response = groupService.getGroupMembers(accessTokenHeader, groupId, page, size, EGetMemberOption.ALL_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/id")
    public ResponseEntity<?> getAllMemberIdsList(@PathVariable("groupId") String groupId) {
        ResDTO<?> response = groupService.getGroupMembers(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/new")
    public ResponseEntity<?> getNewMembersList(
            @RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResDTO<?> response = groupService.getGroupMembers(accessTokenHeader, groupId, page, size, EGetMemberOption.NEW_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/admin")
    public ResponseEntity<?> getAdminMembersList(
            @RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResDTO<?> response = groupService.getGroupMembers(accessTokenHeader, groupId, page, size, EGetMemberOption.ADMIN_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/friend")
    public ResponseEntity<?> getFriendMembersList(
            @RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResDTO<?> response = groupService.getGroupMembers(accessTokenHeader, groupId, page, size, EGetMemberOption.FRIEND_MEMBERS);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/friend/all")
    public ResponseEntity<?> getFriendMembersList(@RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId) {
        ResDTO<?> response = groupService.getAllFriendGroupMemberUserIds(accessTokenHeader, groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/members/pending")
    public ResponseEntity<?> getPendingMembersList(@RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId) {
        ResDTO<?> response = groupService.getPendingMembersList(accessTokenHeader, groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/allow-fetch-post")
    public ResponseEntity<?> getAllowFetchPost(@PathVariable("groupId") String groupId) {
        ResDTO<?> response = groupService.isPrivateGroupOrUserJoined(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping()
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest payload) {
        ResDTO<?> response = groupService.createGroup(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/join/{groupId}")
    public ResponseEntity<?> joinGroup(@PathVariable("groupId") String groupId){
        ResDTO<?> response = groupService.joinGroup(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/invite")
    public ResponseEntity<?> inviteUsers(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody InviteUsersRequest payload
    ) {
        groupService.inviteUsers(tokenHeader, payload);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/member/moderate")
    public ResponseEntity<?> moderateMember(@RequestBody ModerateMemberRequest payload){
        ResDTO<?> response = groupService.moderateMember(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/leave")
    public ResponseEntity<?> leaveGroup(@RequestBody LeaveGroupRequest payload){
        ResDTO<?> response = groupService.handleCancelPendingAndLeaveGroup(payload, EHandleLeaveType.LEAVE);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/promoteToAdmin")
    public ResponseEntity<?> promoteToAdmin(@RequestBody PromoteToAdminRequest payload){
        ResDTO<?> response = groupAdminService.promoteToAdmin(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/member/cancel-pending")
    public ResponseEntity<?> cancelPendingGroup(@RequestBody LeaveGroupRequest payload){
        ResDTO<?> response = groupService.handleCancelPendingAndLeaveGroup(payload, EHandleLeaveType.CANCEL_PENDING);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{groupId}/member/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") String memberId, @PathVariable("groupId") String groupId){
        ResDTO<?> response = groupMemberService.removeMember(groupId, memberId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable("groupId") String groupId, @RequestBody UpdateGroupRequest payload){
        ResDTO<?> response = groupService.updateGroupInfo(groupId, payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> delete(@PathVariable("groupId") String groupId){
        ResDTO<?> response = groupService.deleteGroup(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
