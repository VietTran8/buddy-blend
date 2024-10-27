package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateGroupRequest;
import vn.edu.tdtu.dto.request.ModerateMemberRequest;
import vn.edu.tdtu.dto.request.UpdateGroupRequest;
import vn.edu.tdtu.service.GroupMemberService;
import vn.edu.tdtu.service.GroupService;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@RequestHeader("Authorization") String tokenHeader, @PathVariable("groupId") String groupId){
        ResDTO<?> response = groupService.getGroupById(tokenHeader, groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/min")
    public ResponseEntity<?> getGroupByIdForPost(@PathVariable("groupId") String groupId){
        ResDTO<?> response = groupService.getGroupByIdForPost(groupId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getMyGroups() {
        ResDTO<?> response = groupService.getMyGroups();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{groupId}/member/pending")
    public ResponseEntity<?> getPendingMembersList(@RequestHeader("Authorization") String accessTokenHeader, @PathVariable("groupId") String groupId) {
        ResDTO<?> response = groupService.getPendingMembersList(accessTokenHeader, groupId);

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

    @PostMapping("/member/moderate")
    public ResponseEntity<?> moderateMember(@RequestBody ModerateMemberRequest payload){
        ResDTO<?> response = groupService.moderateMember(payload);

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
