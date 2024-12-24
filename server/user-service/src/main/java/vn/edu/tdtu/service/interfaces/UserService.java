package vn.edu.tdtu.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.MinimizedUserResponse;
import vn.edu.tdtu.model.User;

import java.util.List;

public interface UserService {
    public ResDTO<?> findAll();
    public ResDTO<?> findProfile(String userId);
    public ResDTO<List<MinimizedUserResponse>> getUserSuggestionForGroup(String tokenHeader, String groupId);
    public ResDTO<?> findByEmailResp(String email);
    public User findByEmail(String email);
    public ResDTO<?> findResById(String id);
    public ResDTO<List<MinimizedUserResponse>> findResByIds(String token, FindByIdsReqDTO request);
    public ResDTO<List<MinimizedUserResponse>> findFriendsByNotInIds(String token, FindByIdsReqDTO request);
    public User findById(String id);
    public ResDTO<?> existsById(String id);
    public ResDTO<?> searchByName(String token, String name);
    public ResDTO<?> saveUser(SaveUserReqDTO user);
    public ResDTO<?> updateBio(UpdateBioReqDTO userBio);
    public ResDTO<?> renameUser( RenameReqDTO request);
    public ResDTO<?> updatePicture(MultipartFile pic, boolean isProfilePic);
    public ResDTO<?> updateInfo(UpdateInfoReqDTO request);
    public ResDTO<?> disableAccount(DisableAccountReqDTO account);
    public ResDTO<?> saveUserRegistrationId(SaveUserResIdReq requestBody);
    public ResDTO<?> removeUserRegistrationId(SaveUserResIdReq requestBody);
}
