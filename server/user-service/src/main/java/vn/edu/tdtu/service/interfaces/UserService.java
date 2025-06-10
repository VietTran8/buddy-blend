package vn.edu.tdtu.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.model.User;
import vn.tdtu.common.dto.UserDTO;

import java.util.List;

public interface UserService {
    ResDTO<?> findAll();

    ResDTO<?> findProfile(String userId);

    ResDTO<List<UserDTO>> getUserSuggestionForGroup(String tokenHeader, String groupId);

    ResDTO<?> findByEmailResp(String email);

    User findByEmail(String email);

    ResDTO<?> findResById(String id);

    ResDTO<List<UserDTO>> findResByIds(String token, FindByIdsReqDTO request);

    ResDTO<List<UserDTO>> findFriendsByNotInIds(String token, FindByIdsReqDTO request);

    User findById(String id);

    ResDTO<?> existsById(String id);

    ResDTO<?> searchByName(String token, String name);

    ResDTO<?> saveUser(SaveUserReqDTO user);

    ResDTO<?> updateBio(UpdateBioReqDTO userBio);

    ResDTO<?> renameUser(RenameReqDTO request);

    ResDTO<?> updatePicture(MultipartFile pic, boolean isProfilePic);

    ResDTO<?> updateInfo(UpdateInfoReqDTO request);

    ResDTO<?> disableAccount(DisableAccountReqDTO account);

    ResDTO<?> saveUserRegistrationId(SaveUserResIdReq requestBody);

    ResDTO<?> removeUserRegistrationId(SaveUserResIdReq requestBody);
}
