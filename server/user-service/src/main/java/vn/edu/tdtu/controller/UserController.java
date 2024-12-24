package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.service.interfaces.BanningService;
import vn.edu.tdtu.service.interfaces.FriendRequestService;
import vn.edu.tdtu.service.interfaces.UserFavouriteService;
import vn.edu.tdtu.service.interfaces.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final BanningService banningService;
    private final FriendRequestService friendRequestService;
    private final UserFavouriteService userFavouriteService;

    @GetMapping("")
    public ResponseEntity<?> findAll(){
        ResDTO<?> response = userService.findAll();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo(@RequestParam(name = "id", required = false, defaultValue = "") String id){
        ResDTO<?> response = userService.findProfile(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{email}/for-auth")
    public ResponseEntity<?> findByEmail(@PathVariable("email") String email){
        ResDTO<?> response = userService.findByEmailResp(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id){
        ResDTO<?> response = userService.findResById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/suggestions/group/{groupId}")
    public ResponseEntity<?> findFriendNotInGroup(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable("groupId") String groupId
    ){
        ResDTO<?> response = userService.getUserSuggestionForGroup(tokenHeader, groupId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/by-ids")
    public ResponseEntity<?> findByIds(@RequestHeader(name = "Authorization", required = false, defaultValue = "") String token, @RequestBody FindByIdsReqDTO request){
        ResDTO<?> response = userService.findResByIds(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody SaveUserReqDTO request){
        ResDTO<?> response = userService.saveUser(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/exists/{id}")
    public ResponseEntity<?> exists(@PathVariable("id") String id){
        ResDTO<?> response = userService.existsById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestHeader("Authorization") String token, @RequestParam("key") String key){
        ResDTO<?> response = userService.searchByName(token, key);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/bio/update")
    public ResponseEntity<?> updateBio(@RequestBody UpdateBioReqDTO request){
        ResDTO<?> response = userService.updateBio(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/info/update")
    public ResponseEntity<?> updateBio(@RequestBody UpdateInfoReqDTO request){
        ResDTO<?> response = userService.updateInfo(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/name/update")
    public ResponseEntity<?> updateUserName(@RequestBody RenameReqDTO request){
        ResDTO<?> response = userService.renameUser(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfilePic(@RequestParam("file") MultipartFile file){
        ResDTO<?> response = userService.updatePicture(file, true);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/cover/update")
    public ResponseEntity<?> updateCover(@RequestParam("file") MultipartFile file){
        ResDTO<?> response = userService.updatePicture(file, false);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody DisableAccountReqDTO request){
        ResDTO<?> response = userService.disableAccount(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/friend-req")
    public ResponseEntity<?> handle(@RequestBody FriendReqDTO request){
        ResDTO<?> response = friendRequestService.handleFriendRequest(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/friend-req/acceptation")
    public ResponseEntity<?> acceptation(@RequestBody FQAcceptationDTO request){
        ResDTO<?> response = friendRequestService.friendRequestAcceptation(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend-reqs")
    public ResponseEntity<?> getFriendRequests(@RequestHeader(name = "Authorization") String token){
        ResDTO<?> response = friendRequestService.getListFriendRequestResp(token);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend-req/{fromUserId}")
    public ResponseEntity<?> getFriendRequestByFromUserId(
            @PathVariable("fromUserId") String fromUserId
    ){
        ResDTO<?> response = friendRequestService.getFriendRequestIdByFromUserId(fromUserId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam(name = "id", required = false) String userId
    ){
        ResDTO<?> response = friendRequestService.getListFriendsResp(token, userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friends/suggestions")
    public ResponseEntity<?> getFriendSuggestions(){
        ResDTO<?> response = friendRequestService.getFriendSuggestions();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/favourite")
    public ResponseEntity<?> saveUserFavourite(@RequestBody SaveUserFavouriteDTO request){
        ResDTO<?> response = userFavouriteService.saveUserFavorite(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/favourite")
    public ResponseEntity<?> getUserFavourite(){
        ResDTO<?> response = userFavouriteService.getUserFavourites();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/favourite/{id}")
    public ResponseEntity<?> getUserFavouriteDetail(@RequestHeader(name = "Authorization") String token,
                                                    @PathVariable("id") String favId){
        ResDTO<?> response = userFavouriteService.getUserFavById(token, favId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/favourite/delete/{id}")
    public ResponseEntity<?> deleteUserFavourite(@PathVariable("id") String id){
        ResDTO<?> response = userFavouriteService.deleteUserFavourite(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/registration/save")
    public ResponseEntity<?> saveRegistrationId(@RequestBody SaveUserResIdReq request){
        ResDTO<?> response = userService.saveUserRegistrationId(request);
        log.info("Registration ID: " + request.getRegistrationId());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/registration/remove")
    public ResponseEntity<?> removeRegistrationId(@RequestBody SaveUserResIdReq request){
        ResDTO<?> response = userService.removeUserRegistrationId(request);
        log.info("Registration ID: " + request.getRegistrationId());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/ban/{banUserId}")
    public ResponseEntity<?> handleBanUser(@PathVariable("banUserId") String banUserId){
        ResDTO<?> response = banningService.handleUserBanning(banUserId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/ban")
    public ResponseEntity<?> getBaningUsers(){
        ResDTO<?> response = banningService.getBannedUserList();
        return ResponseEntity.status(response.getCode()).body(response);
    }
}