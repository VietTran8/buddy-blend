package vn.edu.tdtu.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.*;
import vn.edu.tdtu.dtos.response.AuthUserResponse;
import vn.edu.tdtu.dtos.response.MinimizedUserResponse;
import vn.edu.tdtu.dtos.response.SaveUserResponse;
import vn.edu.tdtu.dtos.response.UserDetailsResponse;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.mapper.request.SaveUserReqMapper;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.mapper.response.UserDetailsMapper;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.UserRepository;
import vn.edu.tdtu.utils.JwtUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SaveUserReqMapper saveUserReqMapper;
    private final FileService fileService;
    private final JwtUtils jwtUtils;
    private final FirebaseService firebaseService;
    private final MinimizedUserMapper minimizedUserMapper;
    private final UserDetailsMapper userDetailsMapper;
    private final SendKafkaMsgService kafkaMsgService;

    public ResDTO<?> findAll(){
        ResDTO<List<User>> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("users fetched successfully");
        response.setData(userRepository.findByActive(true));
        return response;
    }

    public MinimizedUserResponse mapToResponse(String token, User user) {
        return minimizedUserMapper.mapToDTO(token, user);
    }

    public ResDTO<?> findByToken(String token, String id){

        String userId = jwtUtils.getUserIdFromJwtToken(token);
        ResDTO<UserDetailsResponse> response = new ResDTO<>();

        User foundUser = userRepository.findByIdAndActive(id.isEmpty() ? userId : id, true).orElse(null);

        if(foundUser != null) {
            response.setCode(HttpServletResponse.SC_OK);
            response.setMessage("users fetched successfully");
            response.setData(userDetailsMapper.mapToDTO(token, foundUser));
        }else{
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setMessage("user not found");
            response.setData(null);
        }

        return response;
    }

    public ResDTO<?> findByEmailResp(String email){
        User foundUser = findByEmail(email);
        ResDTO<AuthUserResponse> response = new ResDTO<>();

        if(foundUser != null) {
            response.setCode(HttpServletResponse.SC_OK);
            response.setMessage("User fetched successfully");
            response.setData(AuthUserResponse.builder()
                            .id(foundUser.getId())
                            .email(foundUser.getEmail())
                            .active(foundUser.isActive())
                            .userAvatar(foundUser.getProfilePicture())
                            .userFullName(foundUser.getUserFullName())
                            .role(foundUser.getRole() == null ? EUserRole.ROLE_USER : foundUser.getRole())
                    .build());
        } else {
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setMessage("User not found with email: " + email);
            response.setData(null);
        }

        return response;
    }

    public User findByEmail(String email){
        return userRepository.findByEmailAndActive(email, true).orElse(null);
    }

    public ResDTO<?> findResById(String token, String id){
        ResDTO<MinimizedUserResponse> response = new ResDTO<>();

        User foundUser = userRepository.findByIdAndActive(id, true).orElse(null);

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("user fetched successfully");
        response.setData(minimizedUserMapper.mapToDTO(token, foundUser));

        return response;
    }

    public ResDTO<?> findResByIds(String token, FindByIdsReqDTO request){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();

        List<MinimizedUserResponse> users = userRepository.findByIdInAndActive(request.getUserIds(), true)
                .stream().map(u -> minimizedUserMapper.mapToDTO(token, u))
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("users fetched successfully");
        response.setData(users);

        return response;
    }

    public User findById(String id){
        return userRepository.findByIdAndActive(id, true).orElse(null);
    }

    public ResDTO<?> existsById(String id){
        ResDTO<Boolean> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");
        response.setData(userRepository.existsById(id));
        return response;
    }

    public ResDTO<?> searchByName(String token, String name){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();

        List<MinimizedUserResponse> userResponses = userRepository.findByNamesContaining(name).stream().map(
                user -> minimizedUserMapper.mapToDTO(token, user)
        ).toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");
        response.setData(userResponses);
        return response;
    }

    public ResDTO<?> saveUser(SaveUserReqDTO user){
        ResDTO<SaveUserResponse> response = new ResDTO<>();
        if(!userRepository.existsByEmail(user.getEmail())){
            User savedUser = userRepository.save(saveUserReqMapper.mapToObject(user));

            response.setCode(HttpServletResponse.SC_OK);
            response.setMessage("Đăng ký thành công");
            response.setData(SaveUserResponse.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .build());

            kafkaMsgService.pubSyncUserData(savedUser, ESyncType.TYPE_CREATE);

        }else{
            response.setData(null);
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setMessage("Email này đã tồn tại!");
        }

        return response;
    }

    public ResDTO<?> updateBio(String token, UpdateBioReqDTO userBio){
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        User user = findById(userId);
        ResDTO<User> response = new ResDTO<>();
        if(user != null){
            user.setBio(userBio.getBio());

            response.setMessage("Cập nhật thành công!");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(userRepository.save(user));
        }else{
            response.setMessage("Không tìm thấy người dùng với id: " + userId);
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);
        }

        return response;
    }

    public ResDTO<?> renameUser(String token, RenameReqDTO request){
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        User user = findById(userId);
        ResDTO<User> response = new ResDTO<>();
        if(user != null){
            user.setFirstName(request.getFirstName());
            user.setMiddleName(request.getMiddleName());
            user.setLastName(request.getLastName());

            response.setMessage("Cập nhật thành công!");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(userRepository.save(user));

            kafkaMsgService.pubSyncUserData(user, ESyncType.TYPE_UPDATE);

        }else{
            response.setMessage("Không tìm thấy người dùng với id " + userId);
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);
        }

        return response;
    }

    public ResDTO<?> updatePicture(String token, MultipartFile pic, boolean isProfilePic){
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        User foundUser = findById(userId);
        ResDTO<User> response = new ResDTO<>();

        if(foundUser != null){
            try{
                String fileName = fileService.upload(pic, EFileType.TYPE_IMG);

                if(isProfilePic)
                    foundUser.setProfilePicture(fileName);
                else
                    foundUser.setCover(fileName);

                response.setData(userRepository.save(foundUser));
                response.setCode(HttpServletResponse.SC_OK);
                response.setMessage("Cập nhật thành công!");
            }catch (Exception e){
                response.setData(null);
                response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                response.setMessage("Lỗi xảy ra: " + e.getMessage());
            }
        }else{
            response.setData(null);
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setMessage("Không tìm thấy người dùng với id: " + userId);
        }

        return response;
    }

    public ResDTO<?> updateGender(String token, UpdateGenderReqDTO userGender){
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        User user = findById(userId);
        ResDTO<User> response = new ResDTO<>();
        if(user != null){
            user.setGender(userGender.getGender());

            response.setMessage("Cập nhật thành công");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(userRepository.save(user));
        }else{
            response.setMessage("Không tìm thấy người dùng với id: " + userId);
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);
        }

        return response;
    }

    public ResDTO<?> disableAccount(DisableAccountReqDTO account){
        User user = findById(account.getUserId());
        ResDTO<User> response = new ResDTO<>();
        if(user != null){
            user.setActive(false);

            response.setMessage("Đã vô hiệu hóa tài khoản");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(userRepository.save(user));

            kafkaMsgService.pubSyncUserData(user, ESyncType.TYPE_DELETE);

        }else{
            response.setMessage("Không tìm thấy người dùng với id: " + account.getUserId());
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);
        }

        return response;
    }

    public ResDTO<?> saveUserRegistrationId(String token, SaveUserResIdReq requestBody){
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        User foundUser = findById(userId);
        Map<String, String> data = new HashMap<>();
        data.put("notificationKey", "");

        ResDTO<Map<String, String>> response = new ResDTO<>();

        if(foundUser != null && foundUser.isActive()){
            firebaseService.saveUserDeviceGroup(foundUser, List.of(requestBody.getRegistrationId()));
            userRepository.save(foundUser);
            data.put("notificationKey", foundUser.getNotificationKey());

            response.setCode(200);
            response.setData(data);
            response.setMessage("Saved user registration id");

            return response;
        }

        response.setCode(400);
        response.setData(data);
        response.setMessage("User not found with id: " + userId);

        return response;
    }

    public ResDTO<?> removeUserRegistrationId(String token, SaveUserResIdReq requestBody){
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        User foundUser = findById(userId);
        Map<String, String> data = new HashMap<>();
        data.put("notificationKey", "");

        ResDTO<Map<String, String>> response = new ResDTO<>();

        if(foundUser != null && foundUser.isActive()){
            firebaseService.removeUserRegistrationId(foundUser, List.of(requestBody.getRegistrationId()));
            userRepository.save(foundUser);
            data.put("notificationKey", foundUser.getNotificationKey());

            response.setCode(200);
            response.setData(data);
            response.setMessage("Removed user registration id");

            return response;
        }

        response.setCode(400);
        response.setData(data);
        response.setMessage("User not found with id: " + userId);

        return response;
    }
}