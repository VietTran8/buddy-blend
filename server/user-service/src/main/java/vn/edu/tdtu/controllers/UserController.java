package vn.edu.tdtu.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.*;
import vn.edu.tdtu.services.FriendRequestService;
import vn.edu.tdtu.services.UserFavouriteService;
import vn.edu.tdtu.services.UserService;
import vn.edu.tdtu.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final FriendRequestService friendRequestService;
    private final UserFavouriteService userFavouriteService;
    @GetMapping("")
    public ResponseEntity<?> findAll(){
        ResDTO<?> response = userService.findAll();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo(@RequestHeader(name = "Authorization") String token, @RequestParam(name = "id", required = false, defaultValue = "") String id){
        ResDTO<?> response = userService.findByToken(token, id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{email}/for-auth")
    public ResponseEntity<?> findByEmail(@PathVariable("email") String email){
        ResDTO<?> response = userService.findByEmailResp(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@RequestHeader(name = "Authorization", required = false, defaultValue = "") String token, @PathVariable("id") String id){
        ResDTO<?> response = userService.findResById(token, id);
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
    public ResponseEntity<?> updateBio(@RequestHeader("Authorization") String token, @RequestBody UpdateBioReqDTO request){
        ResDTO<?> response = userService.updateBio(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/gender/update")
    public ResponseEntity<?> updateBio(@RequestHeader("Authorization") String token, @RequestBody UpdateGenderReqDTO request){
        ResDTO<?> response = userService.updateGender(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/name/update")
    public ResponseEntity<?> updateUserName(@RequestHeader("Authorization") String token, @RequestBody RenameReqDTO request){
        ResDTO<?> response = userService.renameUser(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfilePic(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file){
        ResDTO<?> response = userService.updatePicture(token, file, true);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/cover/update")
    public ResponseEntity<?> updateCover(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file){
        ResDTO<?> response = userService.updatePicture(token, file, false);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody DisableAccountReqDTO request){
        ResDTO<?> response = userService.disableAccount(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/friend-req")
    public ResponseEntity<?> handle(@RequestBody FriendReqDTO request, @RequestHeader(name = "Authorization") String token){
        ResDTO<?> response = friendRequestService.handleFriendRequest(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/friend-req/acceptation")
    public ResponseEntity<?> acceptation(@RequestHeader(name = "Authorization") String token, @RequestBody FQAcceptationDTO request){
        ResDTO<?> response = friendRequestService.friendRequestAcceptation(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friend-reqs")
    public ResponseEntity<?> getFriendRequests(@RequestHeader(name = "Authorization") String token){
        ResDTO<?> response = friendRequestService.getListFriendRequestResp(token);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(@RequestHeader(name = "Authorization") String token){
        ResDTO<?> response = friendRequestService.getListFriendsResp(token);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/favourite")
    public ResponseEntity<?> saveUserFavourite(@RequestHeader(name = "Authorization") String token,
                                               @RequestBody SaveUserFavouriteDTO request){
        ResDTO<?> response = userFavouriteService.saveUserFavorite(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/favourite")
    public ResponseEntity<?> getUserFavourite(@RequestHeader(name = "Authorization") String token){
        ResDTO<?> response = userFavouriteService.getUserFavourites(token);
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
    public ResponseEntity<?> saveRegistrationId(@RequestHeader(name = "Authorization") String token,
                                                @RequestBody SaveUserResIdReq request){
        ResDTO<?> response = userService.saveUserRegistrationId(token, request);
        log.info("Registration ID: " + request.getRegistrationId());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/registration/remove")
    public ResponseEntity<?> removeRegistrationId(@RequestHeader(name = "Authorization") String token,
                                                @RequestBody SaveUserResIdReq request){
        ResDTO<?> response = userService.removeUserRegistrationId(token, request);
        log.info("Registration ID: " + request.getRegistrationId());
        return ResponseEntity.status(response.getCode()).body(response);
    }
}