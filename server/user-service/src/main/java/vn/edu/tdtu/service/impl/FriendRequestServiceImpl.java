package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.FQAcceptationDTO;
import vn.edu.tdtu.dto.request.FriendReqDTO;
import vn.edu.tdtu.dto.response.FriendRequestResponse;
import vn.edu.tdtu.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.mapper.request.AddFriendReqMapper;
import vn.edu.tdtu.mapper.response.FriendRequestResponseMapper;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.message.FriendRequestMessage;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.FriendRequestRepository;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.FriendRequestService;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final AddFriendReqMapper addFriendReqMapper;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final MinimizedUserMapper minimizedUserMapper;
    private final FriendRequestResponseMapper friendRequestResponseMapper;

    private static void rejectFriendRequest(FriendRequest fRequest, Map<String, String> data, ResponseVM<Map<String, String>> response) {
        fRequest.setStatus(EFriendReqStatus.DENIED);

        data.put("status", fRequest.getStatus().name());

        response.setData(data);
        response.setMessage(MessageCode.User.FRIEND_REQUEST_REJECTED);
        response.setCode(HttpServletResponse.SC_OK);
    }

    private static void acceptFriend(FriendRequest fRequest, Map<String, String> data, ResponseVM<Map<String, String>> response) {
        fRequest.setStatus(EFriendReqStatus.ACCEPTED);

        data.put("status", fRequest.getStatus().name());

        response.setData(data);
        response.setMessage(MessageCode.User.FRIEND_REQUEST_ACCEPTED);
        response.setCode(HttpServletResponse.SC_OK);
    }

    private static FriendRequestMessage getFriendRequestMessage(FriendRequest newRequest) {
        FriendRequestMessage notification = new FriendRequestMessage();
        notification.setFromUserId(newRequest.getFromUser().getId());
        notification.setNotificationKey(newRequest.getToUser().getNotificationKey());
        notification.setContent(newRequest.getFromUser().getUserFullName() + " đã gửi cho bạn lời mời kết bạn");
        notification.setTitle("Yêu cầu kết bạn");
        notification.setToUserId(newRequest.getToUser().getId());

        return notification;
    }

    @Override
    public ResponseVM<?> handleFriendRequest(FriendReqDTO request) {
        String fromUserId = SecurityContextUtils.getUserId();
        ResponseVM<HandleFriendRequestResponse> response = new ResponseVM<>();

        log.info(fromUserId);
        if (fromUserId == null) {
            throw new BadRequestException(MessageCode.Authentication.AUTH_UNAUTHORIZED);
        }

        if (fromUserId.equals(request.getToUserId())) {
            throw new BadRequestException(MessageCode.User.FRIEND_REQUEST_CAN_NOT_SEND);
        }

        FriendRequest newRequest = addFriendReqMapper.mapToObject(fromUserId, request);

        if (newRequest.getFromUser() == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, fromUserId);
        }

        if (newRequest.getToUser() == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, request.getToUserId());
        }

        User fromUser = newRequest.getFromUser(),
                toUser = newRequest.getToUser();

        if (friendRequestRepository
                .findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser)
                .stream().anyMatch(req -> fromUserId.equals(req.getToUser().getId()) && req.getStatus().equals(EFriendReqStatus.PENDING))) {

            throw new BadRequestException(MessageCode.User.FRIEND_REQUEST_CAN_NOT_SEND_TO_USER_ID, request.getToUserId());
        }

        List<User> toUserListFriends = getListFriends(toUser.getId());
        List<User> toUserFriendRequestList = getFromUsersViaRequests(
                getListFriendRequest(toUser.getId())
        );

        boolean isFriendOrAlreadySentReq = toUserListFriends.contains(fromUser)
                || toUserFriendRequestList.contains(fromUser);
        boolean isFriend = toUserListFriends.contains(fromUser);

        HandleFriendRequestResponse responseData = new HandleFriendRequestResponse();

        if (!isFriendOrAlreadySentReq) {
            sendFriendRequest(newRequest, responseData, response);
            return response;
        }

        if (isFriend) {
            unfriend(responseData, response, fromUser, toUser);
            return response;
        }

        cancelFriendRequest(responseData, response, fromUser, toUser);
        return response;
    }

    @Override
    public List<User> getListFriends(String userId) {
        return userRepository.findByIdAndActive(userId, true)
                .map(user -> {
                    List<FriendRequest> friendRequests = friendRequestRepository
                            .findByFromUserAndStatusOrToUserAndStatus(user, EFriendReqStatus.ACCEPTED, user, EFriendReqStatus.ACCEPTED);

                    return friendRequests.stream()
                            .map(request -> getFriendFromRequest(request, userId))
                            .filter(User::isActive)
                            .toList();
                })
                .orElse(Collections.emptyList());
    }

    private User getFriendFromRequest(FriendRequest request, String userId) {
        return request.getFromUser().getId().equals(userId)
                ? request.getToUser()
                : request.getFromUser();
    }

    @Override
    public ResponseVM<?> getListFriendsResp(String token, String id) {
        ResponseVM<List<UserDTO>> response = new ResponseVM<>();
        String userId = id == null ? SecurityContextUtils.getUserId() : id;

        List<UserDTO> minimizedUsers = getListFriends(userId).stream().map(
                minimizedUserMapper::mapToDTO
        ).filter(friend -> !friend.isHiddenBanned()).toList();

        if (userId != null) {
            response.setMessage(MessageCode.User.FRIEND_REQUEST_FRIEND_LIST_FETCHED);
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(minimizedUsers);

            return response;
        }

        throw new UnauthorizedException(MessageCode.Authentication.AUTH_UNAUTHORIZED);
    }

    @Override
    public ResponseVM<?> getFriendIds(String userId) {
        ResponseVM<List<String>> response = new ResponseVM<>();

        List<User> minimizedUsers = getListFriends(userId);

        if (userId != null) {
            response.setMessage(MessageCode.User.FRIEND_REQUEST_FRIEND_LIST_FETCHED);
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(minimizedUsers.stream().map(User::getId).toList());

            return response;
        }

        throw new BadRequestException(MessageCode.User.USER_ID_NOT_NULL);
    }

    @Override
    public List<FriendRequest> getListFriendRequest(String userId) {
        User foundUser = userRepository.findByIdAndActive(userId, true).orElse(null);

        if (foundUser == null) {
            return new ArrayList<>();
        }

        return friendRequestRepository.findByFromUserAndStatusOrToUserAndStatus(foundUser, EFriendReqStatus.PENDING, foundUser, EFriendReqStatus.PENDING);
    }

    @Override
    public ResponseVM<?> getListFriendRequestResp(String token) {
        ResponseVM<List<FriendRequestResponse>> response = new ResponseVM<>();

        String userId = SecurityContextUtils.getUserId();

        if (userId == null) {
            throw new UnauthorizedException(MessageCode.Authentication.AUTH_UNAUTHORIZED);
        }

        response.setMessage(MessageCode.User.FRIEND_REQUEST_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(
                getListFriendRequest(userId)
                        .stream()
                        .filter(request -> request.getToUser().getId().equals(userId))
                        .map(friendRequestResponseMapper::mapToDTO)
                        .filter(resp -> !resp.getFromUser().isHiddenBanned())
                        .toList()
        );

        return response;
    }

    @Override
    public ResponseVM<?> getFriendRequestIdByFromUserId(String fromUserId) {
        FriendRequest foundFriendRequest = friendRequestRepository.findByFromUserIdAndToUserId(fromUserId, SecurityContextUtils.getUserId())
                .orElseThrow(() -> new BadRequestException(MessageCode.User.FRIEND_REQUEST_NOT_FOUND));

        return new ResponseVM<>(
                MessageCode.User.FRIEND_REQUEST_FETCHED,
                foundFriendRequest.getId(),
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public List<User> getFromUsersViaRequests(List<FriendRequest> friendRequests) {
        return friendRequests.stream().map(FriendRequest::getFromUser).filter(User::isActive).toList();
    }

    @Override
    public ResponseVM<?> friendRequestAcceptation(FQAcceptationDTO request) {
        ResponseVM<Map<String, String>> response = new ResponseVM<>();
        Map<String, String> data = new HashMap<>();

        FriendRequest fRequest = friendRequestRepository.findById(request.getFriendReqId()).orElseThrow(
                () -> new BadRequestException(MessageCode.User.FRIEND_REQUEST_NOT_FOUND)
        );

        if (!fRequest.getToUser().getId().equals(SecurityContextUtils.getUserId())) {
            throw new BadRequestException(MessageCode.Authentication.AUTH_NOT_PERMITTED);
        }

        data.put("requestId", fRequest.getId());
        data.put("status", fRequest.getStatus().name());

        if (!fRequest.getStatus().equals(EFriendReqStatus.PENDING))
            throw new BadRequestException(MessageCode.User.FRIEND_REQUEST_JUST_HANDLE_PENDING);

        if (request.getIsAccept()) {
            acceptFriend(fRequest, data, response);
            friendRequestRepository.save(fRequest);

        } else {
            rejectFriendRequest(fRequest, data, response);
            friendRequestRepository.delete(fRequest);

        }

        return response;
    }

    @Override
    public ResponseVM<?> getFriendSuggestions() {
        String authUserId = SecurityContextUtils.getUserId();

        List<User> myFriends = getListFriends(authUserId);
        Set<String> myFriendIds = myFriends.stream().map(User::getId).collect(Collectors.toSet());

        Set<User> suggestions = new LinkedHashSet<>();

        myFriends.forEach(friend -> {
            List<User> friendsFriends = Optional.ofNullable(getListFriends(friend.getId()))
                    .orElse(Collections.emptyList());

            friendsFriends.stream()
                    .filter(u -> !myFriendIds.contains(u.getId()) && !u.getId().equals(authUserId))
                    .forEach(suggestions::add);
        });

        List<UserDTO> responseData = suggestions.stream()
                .map(minimizedUserMapper::mapToDTO)
                .sorted(Comparator.comparing(UserDTO::getUserFullName))
                .toList();

        ResponseVM<List<UserDTO>> response = new ResponseVM<>();
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.FRIEND_REQUEST_SUGGESTION_FETCHED);

        return response;

    }

    private void sendFriendRequest(FriendRequest newRequest, HandleFriendRequestResponse responseData, ResponseVM<HandleFriendRequestResponse> response) {
        friendRequestRepository.save(newRequest);

        responseData.setRequestId(newRequest.getId());
        responseData.setStatus(newRequest.getStatus());

        FriendRequestMessage notification = getFriendRequestMessage(newRequest);

        kafkaEventPublisher.pubFriendRequestNoti(notification);

        response.setData(responseData);
        response.setMessage(MessageCode.User.FRIEND_REQUEST_SENT);
        response.setCode(HttpServletResponse.SC_OK);
    }

    private void unfriend(HandleFriendRequestResponse data, ResponseVM<HandleFriendRequestResponse> response, User fromUser, User toUser) {
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser).stream().filter(req -> req.isActive() && req.getStatus().equals(EFriendReqStatus.ACCEPTED)).toList();
        FriendRequest friendRequest = friendRequests.stream().findFirst().orElse(null);

        if (friendRequest == null) {
            throw new BadRequestException(MessageCode.User.FRIEND_REQUEST_NOT_FOUND);
        }

        friendRequestRepository.delete(friendRequest);
        data.setRequestId(friendRequest.getId());
        data.setStatus(EFriendReqStatus.CANCELLED);

        response.setData(data);
        response.setMessage(MessageCode.User.FRIEND_REQUEST_UNFRIENDED);
        response.setCode(HttpServletResponse.SC_OK);
    }

    private void cancelFriendRequest(HandleFriendRequestResponse data, ResponseVM<HandleFriendRequestResponse> response, User fromUser, User toUser) {
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser).stream().filter(req -> req.isActive() && req.getStatus().equals(EFriendReqStatus.PENDING)).toList();
        FriendRequest friendRequest = friendRequests.stream().findFirst().orElse(null);

        if (friendRequest == null) {
            throw new BadRequestException(MessageCode.User.FRIEND_REQUEST_NOT_FOUND);
        }

        friendRequestRepository.delete(friendRequest);
        data.setRequestId(friendRequest.getId());
        data.setStatus(EFriendReqStatus.CANCELLED);

        response.setData(data);
        response.setMessage(MessageCode.User.FRIEND_REQUEST_CANCELLED);
        response.setCode(HttpServletResponse.SC_OK);
    }
}
