package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.service.interfaces.BlockingService;
import vn.edu.tdtu.service.interfaces.FriendRequestService;
import vn.edu.tdtu.service.interfaces.UserFavouriteService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_USER)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final BlockingService blockingService;
    private final FriendRequestService friendRequestService;
    private final UserFavouriteService userFavouriteService;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        ResponseVM<?> response = userService.findAll();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo(@RequestParam(name = "id", required = false, defaultValue = "") String id) {
        ResponseVM<?> response = userService.findProfile(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/for-auth/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable("email") String email) {
        ResponseVM<?> response = userService.findByEmailResp(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        ResponseVM<?> response = userService.findResById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/suggestions/group/{groupId}")
    public ResponseEntity<?> findFriendNotInGroup(@PathVariable("groupId") String groupId) {
        ResponseVM<?> response = userService.getUserSuggestionForGroup(groupId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<?> findByIds(@RequestBody FindByIdsReqDTO request) {
        ResponseVM<?> response = userService.findResByIds(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody SaveUserReqDTO request) {
        ResponseVM<?> response = userService.saveUser(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/exists/{id}")
    public ResponseEntity<?> exists(@PathVariable("id") String id) {
        ResponseVM<?> response = userService.existsById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("key") String key) {
        ResponseVM<?> response = userService.searchByName(key);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/bio/update")
    public ResponseEntity<?> updateBio(@RequestBody UpdateBioReqDTO request) {
        ResponseVM<?> response = userService.updateBio(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/info/update")
    public ResponseEntity<?> updateBio(@RequestBody UpdateInfoReqDTO request) {
        ResponseVM<?> response = userService.updateInfo(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/name/update")
    public ResponseEntity<?> updateUserName(@RequestBody RenameReqDTO request) {
        ResponseVM<?> response = userService.renameUser(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfilePic(@RequestParam("file") MultipartFile file) {
        ResponseVM<?> response = userService.updatePicture(file, true);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/cover/update")
    public ResponseEntity<?> updateCover(@RequestParam("file") MultipartFile file) {
        ResponseVM<?> response = userService.updatePicture(file, false);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody DisableAccountReqDTO request) {
        ResponseVM<?> response = userService.disableAccount(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/friend-req")
    public ResponseEntity<?> handle(@RequestBody FriendReqDTO request) {
        ResponseVM<?> response = friendRequestService.handleFriendRequest(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/friend-req/acceptation")
    public ResponseEntity<?> acceptation(@RequestBody FQAcceptationDTO request) {
        ResponseVM<?> response = friendRequestService.friendRequestAcceptation(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend-reqs")
    public ResponseEntity<?> getFriendRequests() {
        ResponseVM<?> response = friendRequestService.getListFriendRequestResp();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend-req/{fromUserId}")
    public ResponseEntity<?> getFriendRequestByFromUserId(
            @PathVariable("fromUserId") String fromUserId
    ) {
        ResponseVM<?> response = friendRequestService.getFriendRequestIdByFromUserId(fromUserId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(@RequestParam(name = "id", required = false) String userId) {
        ResponseVM<?> response = friendRequestService.getListFriendsResp(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friends/id")
    public ResponseEntity<?> getFriendIds(@RequestParam(name = "id", required = true) String userId) {
        ResponseVM<?> response = friendRequestService.getFriendIds(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friends/suggestions")
    public ResponseEntity<?> getFriendSuggestions() {
        ResponseVM<?> response = friendRequestService.getFriendSuggestions();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/favourite")
    public ResponseEntity<?> saveUserFavourite(@RequestBody SaveUserFavouriteDTO request) {
        ResponseVM<?> response = userFavouriteService.saveUserFavorite(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/favourite")
    public ResponseEntity<?> getUserFavourite() {
        ResponseVM<?> response = userFavouriteService.getUserFavourites();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/favourite/{id}")
    public ResponseEntity<?> getUserFavouriteDetail(
                                                    @PathVariable("id") String favId) {
        ResponseVM<?> response = userFavouriteService.getUserFavById(favId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/favourite/delete/{id}")
    public ResponseEntity<?> deleteUserFavourite(@PathVariable("id") String id) {
        ResponseVM<?> response = userFavouriteService.deleteUserFavourite(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/registration/save")
    public ResponseEntity<?> saveRegistrationId(@RequestBody SaveUserResIdReq request) {
        ResponseVM<?> response = userService.saveUserRegistrationId(request);
        log.info("Registration ID: " + request.getRegistrationId());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/registration/remove")
    public ResponseEntity<?> removeRegistrationId(@RequestBody SaveUserResIdReq request) {
        ResponseVM<?> response = userService.removeUserRegistrationId(request);
        log.info("Registration ID: " + request.getRegistrationId());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/block/{blockUserId}")
    public ResponseEntity<?> handleBlockUser(@PathVariable("blockUserId") String banUserId) {
        ResponseVM<?> response = blockingService.handleUserBlocking(banUserId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/block")
    public ResponseEntity<?> getBlockedUsers() {
        ResponseVM<?> response = blockingService.getBlockedUserList();
        return ResponseEntity.status(response.getCode()).body(response);
    }
}