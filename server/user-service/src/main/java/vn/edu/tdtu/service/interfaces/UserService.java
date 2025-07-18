package vn.edu.tdtu.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.model.User;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

public interface UserService {
    ResponseVM<?> findAll();

    ResponseVM<?> findProfile(String userId);

    ResponseVM<List<UserDTO>> getUserSuggestionForGroup(String groupId);

    ResponseVM<?> findByEmailResp(String email);

    User findByEmail(String email);

    ResponseVM<?> findResById(String id);

    ResponseVM<List<UserDTO>> findResByIds(FindByIdsReqDTO request);

    ResponseVM<List<UserDTO>> findFriendsByNotInIds(FindByIdsReqDTO request);

    User findById(String id);

    ResponseVM<?> existsById(String id);

    ResponseVM<?> searchByName(String name);

    ResponseVM<?> saveUser(SaveUserReqDTO user);

    ResponseVM<?> updateBio(UpdateBioReqDTO userBio);

    ResponseVM<?> renameUser(RenameReqDTO request);

    ResponseVM<?> updatePicture(MultipartFile pic, boolean isProfilePic);

    ResponseVM<?> updateInfo(UpdateInfoReqDTO request);

    ResponseVM<?> disableAccount(DisableAccountReqDTO account);

    ResponseVM<?> saveUserRegistrationId(SaveUserResIdReq requestBody);

    ResponseVM<?> removeUserRegistrationId(SaveUserResIdReq requestBody);
}
