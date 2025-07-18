package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.AuthUserResponse;
import vn.edu.tdtu.dto.response.SaveUserResponse;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.mapper.request.SaveUserReqMapper;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.mapper.response.UserDetailsMapper;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.*;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.search.ESyncType;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

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
    public ResponseVM<?> findAll() {
        ResponseVM<List<User>> response = new ResponseVM<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(userRepository.findByActive(true));

        return response;
    }

    @Override
    public ResponseVM<?> findProfile(String id) {
        String userId = SecurityContextUtils.getUserId();
        ResponseVM<UserDTO> response = new ResponseVM<>();

        User foundUser = userRepository.findByIdAndActive(id.isEmpty() ? userId : id, true)
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND));

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(userDetailsMapper.mapToDTO(foundUser));

        return response;
    }

    @Override
    public ResponseVM<List<UserDTO>> getUserSuggestionForGroup(String groupId) {
        List<String> friendUserIdsInGroup = groupService.getFriendUserIdsInGroup(groupId);

        return findFriendsByNotInIds(new FindByIdsReqDTO(friendUserIdsInGroup));
    }

    @Override
    public ResponseVM<?> findByEmailResp(String email) {
        User foundUser = findByEmail(email);
        ResponseVM<AuthUserResponse> response = new ResponseVM<>();

        if (foundUser == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_EMAIL, email);
        }

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
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
    public User findByEmail(String email) {
        return userRepository.findByEmailAndActive(email, true).orElse(null);
    }

    @Override
    public ResponseVM<?> findResById(String id) {
        ResponseVM<UserDTO> response = new ResponseVM<>();

        User foundUser = userRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND));

        UserDTO mappedUser = minimizedUserMapper.mapToDTO(foundUser);

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(mappedUser.isHiddenBanned() ? null : mappedUser);

        return response;
    }

    @Override
    public ResponseVM<List<UserDTO>> findResByIds(FindByIdsReqDTO request) {
        ResponseVM<List<UserDTO>> response = new ResponseVM<>();

        List<UserDTO> users = userRepository.findByIdInAndActive(request.getUserIds(), true)
                .stream()
                .map(minimizedUserMapper::mapToDTO)
                .filter(user -> !user.isHiddenBanned())
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(users);

        return response;
    }

    @Override
    public ResponseVM<List<UserDTO>> findFriendsByNotInIds(FindByIdsReqDTO request) {
        ResponseVM<List<UserDTO>> response = new ResponseVM<>();

        String userId = SecurityContextUtils.getUserId();

        List<User> userFriends = friendRequestService.getListFriends(userId);

        List<UserDTO> users = userFriends
                .stream()
                .filter(u -> request.getUserIds().stream().noneMatch(id -> id.equals(u.getId())))
                .map(minimizedUserMapper::mapToDTO)
                .filter(u -> !u.isHiddenBanned())
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(users);

        return response;
    }

    @Override
    public User findById(String id) {
        return userRepository.findByIdAndActive(id, true).orElse(null);
    }

    @Override
    public ResponseVM<?> existsById(String id) {
        ResponseVM<Boolean> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(userRepository.existsById(id));
        return response;
    }

    @Override
    public ResponseVM<?> searchByName(String name) {
        ResponseVM<List<UserDTO>> response = new ResponseVM<>();

        List<UserDTO> userResponses = userRepository.findByNamesContaining(name).stream().map(
                        minimizedUserMapper::mapToDTO
                )
                .filter(u -> !u.isHiddenBanned())
                .toList();

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(userResponses);
        return response;
    }

    @Override
    public ResponseVM<?> saveUser(SaveUserReqDTO user) {
        ResponseVM<SaveUserResponse> response = new ResponseVM<>();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException(MessageCode.User.USER_EMAIL_EXISTS);
        }

        User savedUser = userRepository.save(saveUserReqMapper.mapToObject(user));

        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.USER_SAVED);
        response.setData(SaveUserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .build());

        kafkaMsgService.pubSyncUserData(savedUser, ESyncType.TYPE_CREATE);

        return response;
    }

    @Override
    public ResponseVM<?> updateBio(UpdateBioReqDTO userBio) {
        String userId = SecurityContextUtils.getUserId();
        User user = findById(userId);
        ResponseVM<User> response = new ResponseVM<>();

        if (user == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userId);
        }

        user.setBio(userBio.getBio());

        response.setMessage(MessageCode.User.USER_UPDATED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        return response;
    }

    @Override
    public ResponseVM<?> renameUser(RenameReqDTO request) {
        String userId = SecurityContextUtils.getUserId();
        User user = findById(userId);
        ResponseVM<User> response = new ResponseVM<>();

        if (user == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userId);
        }

        ConfirmTokenCheckingRequest checkingRequest = new ConfirmTokenCheckingRequest();
        checkingRequest.setEmail(user.getEmail());
        checkingRequest.setToken(request.getToken());

        if (!authService.confirmationTokenChecking(checkingRequest))
            throw new BadRequestException(MessageCode.Authentication.AUTH_INVALID_TOKEN);

        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());

        if (request.getMiddleName() != null)
            user.setMiddleName(request.getMiddleName());

        if (request.getLastName() != null)
            user.setLastName(request.getLastName());

        response.setMessage(MessageCode.User.USER_UPDATED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        kafkaMsgService.pubSyncUserData(user, ESyncType.TYPE_UPDATE);

        return response;
    }

    @Override
    public ResponseVM<?> updatePicture(MultipartFile pic, boolean isProfilePic) {
        String userId = SecurityContextUtils.getUserId();
        User foundUser = findById(userId);
        ResponseVM<User> response = new ResponseVM<>();

        if (foundUser == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userId);
        }

        try {
            String fileName = fileService.upload(pic, EFileType.TYPE_IMG);

            if (isProfilePic)
                foundUser.setProfilePicture(fileName);
            else
                foundUser.setCover(fileName);

            response.setData(userRepository.save(foundUser));
            response.setCode(HttpServletResponse.SC_OK);
            response.setMessage(MessageCode.User.USER_UPDATED);

        } catch (Exception e) {
            throw new BadRequestException(String.format("Lỗi xảy ra: %s", e.getMessage()));
        }

        return response;
    }

    @Override
    public ResponseVM<?> updateInfo(UpdateInfoReqDTO request) {
        String userId = SecurityContextUtils.getUserId();
        User user = findById(userId);
        ResponseVM<User> response = new ResponseVM<>();

        if (user == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userId);
        }

        if (request.getGender() != null) user.setGender(request.getGender());

        if (request.getFromCity() != null) user.setFromCity(request.getFromCity());

        if (request.getPhone() != null) user.setPhone(request.getPhone());

        if (request.getBio() != null) user.setBio(request.getBio());

        response.setMessage(MessageCode.User.USER_UPDATED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        return response;
    }

    @Override
    public ResponseVM<?> disableAccount(DisableAccountReqDTO account) {
        User user = findById(account.getUserId());
        ResponseVM<User> response = new ResponseVM<>();

        if (user == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, account.getUserId());
        }

        user.setActive(false);

        response.setMessage(MessageCode.User.USER_DISABLED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(userRepository.save(user));

        kafkaMsgService.pubSyncUserData(user, ESyncType.TYPE_DELETE);

        return response;
    }

    @Override
    public ResponseVM<?> saveUserRegistrationId(SaveUserResIdReq requestBody) {
        String userId = SecurityContextUtils.getUserId();
        User foundUser = findById(userId);
        Map<String, String> data = new HashMap<>();
        data.put("notificationKey", "");

        ResponseVM<Map<String, String>> response = new ResponseVM<>();

        if (foundUser != null && foundUser.isActive()) {
            firebaseService.saveUserDeviceGroup(foundUser, List.of(requestBody.getRegistrationId()));
            userRepository.save(foundUser);
            data.put("notificationKey", foundUser.getNotificationKey());

            response.setCode(200);
            response.setData(data);
            response.setMessage(MessageCode.User.USER_SAVED_REGISTRATION_ID);

            return response;
        }

        throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userId);
    }

    @Override
    public ResponseVM<?> removeUserRegistrationId(SaveUserResIdReq requestBody) {
        String userId = SecurityContextUtils.getUserId();
        User foundUser = findById(userId);
        Map<String, String> data = new HashMap<>();
        data.put("notificationKey", "");

        ResponseVM<Map<String, String>> response = new ResponseVM<>();

        if (foundUser != null && foundUser.isActive()) {
            firebaseService.removeUserRegistrationId(foundUser, List.of(requestBody.getRegistrationId()));
            userRepository.save(foundUser);
            data.put("notificationKey", foundUser.getNotificationKey());

            response.setCode(200);
            response.setData(data);
            response.setMessage(MessageCode.User.USER_DELETED_REGISTRATION_ID);

            return response;
        }

        throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userId);
    }
}