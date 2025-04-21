package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.AuthUserResponse;
import vn.edu.tdtu.dto.response.MinimizedUserResponse;
import vn.edu.tdtu.dto.response.SaveUserResponse;
import vn.edu.tdtu.dto.response.UserDetailsResponse;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.request.SaveUserReqMapper;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.mapper.response.UserDetailsMapper;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.*;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SaveUserReqMapper saveUserReqMapper;
    private final FileService fileService;
    private final FirebaseService firebaseService;
    private final FriendRequestService friendRequestService;
    private final MinimizedUserMapper minimizedUserMapper;
    private final UserDetailsMapper userDetailsMapper;
    private final KafkaEventPublisher kafkaMsgService;
    private final GroupService groupService;
    private final AuthService authService;

    @Override
    public ResDTO<?> findAll(){
        ResDTO<List<User>> response = new ResDTO<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("users fetched successfully");
        response.setData(userRepository.findByActive(true));

        return response;
    }

    @Override
    public ResDTO<?> findProfile(String id){
        String userId = SecurityContextUtils.getUserId();
        ResDTO<UserDetailsResponse> response = new ResDTO<>();

        User foundUser = userRepository.findByIdAndActive(id.isEmpty() ? userId : id, true)
                        .orElseThrow(() -> new BadRequestException("User not found"));

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("users fetched successfully");
        response.setData(userDetailsMapper.mapToDTO(foundUser));

        return response;
    }

    @Override
    public ResDTO<List<MinimizedUserResponse>> getUserSuggestionForGroup(String tokenHeader, String groupId) {
        List<String> friendUserIdsInGroup = groupService.getFriendUserIdsInGroup(tokenHeader, groupId);

        return findFriendsByNotInIds(tokenHeader, new FindByIdsReqDTO(friendUserIdsInGroup));
    }

    @Override
    public ResDTO<?> findByEmailResp(String email){
        User foundUser = findByEmail(email);
        ResDTO<AuthUserResponse> response = new ResDTO<>();

        if(foundUser == null) {
            throw new BadRequestException(String.format("User not found with email: %s", email));
        }

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

        return response;
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmailAndActive(email, true).orElse(null);
    }

    @Override
    public ResDTO<?> findResById(String id){
        ResDTO<MinimizedUserResponse> response = new ResDTO<>();

        User foundUser = userRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new BadRequestException("User not found"));

        MinimizedUserResponse mappedUser = minimizedUserMapper.mapToDTO(foundUser);

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("user fetched successfully");
        response.setData(mappedUser.isHiddenBanned() ? null : mappedUser);

        return response;
    }

    @Override
    public ResDTO<List<MinimizedUserResponse>> findResByIds(String token, FindByIdsReqDTO request){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();

        List<MinimizedUserResponse> users = userRepository.findByIdInAndActive(request.getUserIds(), true)
                .stream()
                .map(minimizedUserMapper::mapToDTO)
                .filter(user -> !user.isHiddenBanned())
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("users fetched successfully");
        response.setData(users);

        return response;
    }

    @Override
    public ResDTO<List<MinimizedUserResponse>> findFriendsByNotInIds(String token, FindByIdsReqDTO request){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();

        String userId = SecurityContextUtils.getUserId();

        List<User> userFriends = friendRequestService.getListFriends(userId);

        List<MinimizedUserResponse> users = userFriends
                .stream()
                .filter(u -> request.getUserIds().stream().noneMatch(id -> id.equals(u.getId())))
                .map(minimizedUserMapper::mapToDTO)
                .filter(u -> !u.isHiddenBanned())
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("users fetched successfully");
        response.setData(users);

        return response;
    }

    @Override
    public User findById(String id){
        return userRepository.findByIdAndActive(id, true).orElse(null);
    }

    @Override
    public ResDTO<?> existsById(String id){
        ResDTO<Boolean> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");
        response.setData(userRepository.existsById(id));
        return response;
    }

    @Override
    public ResDTO<?> searchByName(String token, String name){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();

        List<MinimizedUserResponse> userResponses = userRepository.findByNamesContaining(name).stream().map(
                minimizedUserMapper::mapToDTO
        )
                .filter(u -> !u.isHiddenBanned())
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");
        response.setData(userResponses);
        return response;
    }

    @Override
    public ResDTO<?> saveUser(SaveUserReqDTO user){
        ResDTO<SaveUserResponse> response = new ResDTO<>();

        if(userRepository.existsByEmail(user.getEmail())){
            throw new BadRequestException("Email này đã tồn tại!");
        }

        User savedUser = userRepository.save(saveUserReqMapper.mapToObject(user));

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Đăng ký thành công");
        response.setData(SaveUserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .build());

        kafkaMsgService.pubSyncUserData(savedUser, ESyncType.TYPE_CREATE);

        return response;
    }

    @Override
    public ResDTO<?> updateBio(UpdateBioReqDTO userBio){
        String userId = SecurityContextUtils.getUserId();
        User user = findById(userId);
        ResDTO<User> response = new ResDTO<>();

        if(user == null){
            throw new BadRequestException(String.format("Không tìm thấy người dùng với id: %s", userId));
        }

        user.setBio(userBio.getBio());

        response.setMessage("Cập nhật thành công!");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        return response;
    }

    @Override
    public ResDTO<?> renameUser( RenameReqDTO request){
        String userId = SecurityContextUtils.getUserId();
        User user = findById(userId);
        ResDTO<User> response = new ResDTO<>();

        if(user == null){
            throw new BadRequestException(String.format("Không tìm thấy người dùng với id: %s", userId));
        }

        ConfirmTokenCheckingRequest checkingRequest = new ConfirmTokenCheckingRequest();
        checkingRequest.setEmail(user.getEmail());
        checkingRequest.setToken(request.getToken());

        if(!authService.confirmationTokenChecking(checkingRequest))
            throw new BadRequestException("Invalid token!");

        if(request.getFirstName() != null)
            user.setFirstName(request.getFirstName());

        if (request.getMiddleName() != null)
            user.setMiddleName(request.getMiddleName());

        if(request.getLastName() != null)
            user.setLastName(request.getLastName());

        response.setMessage("Cập nhật thành công!");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        kafkaMsgService.pubSyncUserData(user, ESyncType.TYPE_UPDATE);

        return response;
    }

    @Override
    public ResDTO<?> updatePicture(MultipartFile pic, boolean isProfilePic){
        String userId = SecurityContextUtils.getUserId();
        User foundUser = findById(userId);
        ResDTO<User> response = new ResDTO<>();

        if(foundUser == null){
            throw new BadRequestException(String.format("Không tìm thấy người dùng với id: %s", userId));
        }

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
            throw new BadRequestException(String.format("Lỗi xảy ra: %s", e.getMessage()));
        }

        return response;
    }

    @Override
    public ResDTO<?> updateInfo(UpdateInfoReqDTO request){
        String userId = SecurityContextUtils.getUserId();
        User user = findById(userId);
        ResDTO<User> response = new ResDTO<>();

        if(user == null){
            throw new BadRequestException(String.format("Không tìm thấy người dùng với id: %s", userId));
        }

        if(request.getGender() != null) user.setGender(request.getGender());

        if(request.getFromCity() != null) user.setFromCity(request.getFromCity());

        if(request.getPhone() != null) user.setPhone(request.getPhone());

        if(request.getBio() != null) user.setBio(request.getBio());

        response.setMessage("Cập nhật thành công");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        return response;
    }

    @Override
    public ResDTO<?> disableAccount(DisableAccountReqDTO account){
        User user = findById(account.getUserId());
        ResDTO<User> response = new ResDTO<>();

        if(user == null){
            throw new BadRequestException(String.format("Không tìm thấy người dùng với id: %s", account.getUserId()));
        }

        user.setActive(false);

        response.setMessage("Đã vô hiệu hóa tài khoản");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        kafkaMsgService.pubSyncUserData(user, ESyncType.TYPE_DELETE);

        return response;
    }

    @Override
    public ResDTO<?> saveUserRegistrationId(SaveUserResIdReq requestBody){
        String userId = SecurityContextUtils.getUserId();
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

        throw new BadRequestException("User not found with id: " + userId);
    }

    @Override
    public ResDTO<?> removeUserRegistrationId(SaveUserResIdReq requestBody){
        String userId = SecurityContextUtils.getUserId();
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

        throw new BadRequestException("User not found with id: " + userId);
    }
}