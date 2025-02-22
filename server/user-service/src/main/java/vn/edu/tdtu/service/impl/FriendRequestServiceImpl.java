package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FQAcceptationDTO;
import vn.edu.tdtu.dto.request.FriendReqDTO;
import vn.edu.tdtu.message.FriendRequestMessage;
import vn.edu.tdtu.dto.response.FriendRequestResponse;
import vn.edu.tdtu.dto.response.HandleFriendRequestResponse;
import vn.edu.tdtu.dto.response.MinimizedUserResponse;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.mapper.request.AddFriendReqMapper;
import vn.edu.tdtu.mapper.response.FriendRequestResponseMapper;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.FriendRequestRepository;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.FriendRequestService;
import vn.edu.tdtu.util.JwtUtils;
import vn.edu.tdtu.util.SecurityContextUtils;

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

    public ResDTO<?> handleFriendRequest(FriendReqDTO request){
        String fromUserId = SecurityContextUtils.getUserId();
        ResDTO<HandleFriendRequestResponse> response = new ResDTO<>();

        log.info(fromUserId);
        if(fromUserId == null) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        if(fromUserId.equals(request.getToUserId())) {
            throw new BadRequestException("Không thể gửi lời mời kết bạn");
        }

        FriendRequest newRequest = addFriendReqMapper.mapToObject(fromUserId, request);

        if(newRequest.getFromUser() == null) {
            throw new BadRequestException("Không tìm thấy người dùng với id: " + fromUserId);
        }

        if(newRequest.getToUser() == null) {
            throw new BadRequestException("Không tìm thấy người dùng với id: " + request.getToUserId());
        }

        User fromUser = newRequest.getFromUser(),
                toUser = newRequest.getToUser();

        if(friendRequestRepository
                .findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser)
                .stream().anyMatch(req -> fromUserId.equals(req.getToUser().getId()) && req.getStatus().equals(EFriendReqStatus.PENDING))) {

            throw new BadRequestException("Can not send friend request to user id: " + request.getToUserId());
        }

        List<User> toUserListFriends = getListFriends(toUser.getId());
        List<User> toUserFriendRequestList = getFromUsersViaRequests(
                getListFriendRequest(toUser.getId())
        );

        boolean isFriendOrAlreadySentReq = toUserListFriends.contains(fromUser)
                || toUserFriendRequestList.contains(fromUser);
        boolean isFriend = toUserListFriends.contains(fromUser);

        HandleFriendRequestResponse responseData = new HandleFriendRequestResponse();

        if(!isFriendOrAlreadySentReq) {
            sendFriendRequest(newRequest, responseData, response);
            return response;
        }

        if(isFriend) {
            unfriend(responseData, response, fromUser, toUser);
            return response;
        }

        cancelFriendRequest(responseData, response, fromUser,toUser);
        return response;
    }

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

    public ResDTO<?> getListFriendsResp(String token, String id){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();
        String userId = id == null ? SecurityContextUtils.getUserId() : id;

        List<MinimizedUserResponse> minimizedUsers = getListFriends(userId).stream().map(
                minimizedUserMapper::mapToDTO
        ).filter(friend -> !friend.isHiddenBanned()).toList();

        if(userId != null){
            response.setMessage("friends list fetched successfully");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(minimizedUsers);

            return response;
        }

        throw new UnauthorizedException("You are not authenticated");
    }

    @Override
    public ResDTO<?> getFriendIds(String userId) {
        ResDTO<List<String>> response = new ResDTO<>();

        List<User> minimizedUsers = getListFriends(userId);

        if(userId != null){
            response.setMessage("friends list fetched successfully");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(minimizedUsers.stream().map(User::getId).toList());

            return response;
        }

        throw new BadRequestException("User id can not be null");
    }

    public List<FriendRequest> getListFriendRequest(String userId){
        User foundUser = userRepository.findByIdAndActive(userId, true).orElse(null);

        if(foundUser == null){
            return new ArrayList<>();
        }

        return friendRequestRepository.findByFromUserAndStatusOrToUserAndStatus(foundUser, EFriendReqStatus.PENDING, foundUser, EFriendReqStatus.PENDING);
    }

    public ResDTO<?> getListFriendRequestResp(String token){
        ResDTO<List<FriendRequestResponse>> response = new ResDTO<>();

        String userId = SecurityContextUtils.getUserId();

        if(userId == null){
            throw new UnauthorizedException("You are not authenticated");
        }

        response.setMessage("friend request list fetched successfully");
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
    public ResDTO<?> getFriendRequestIdByFromUserId(String fromUserId) {
        FriendRequest foundFriendRequest = friendRequestRepository.findByFromUserIdAndToUserId(fromUserId, SecurityContextUtils.getUserId())
                .orElseThrow(() -> new BadRequestException("Friend request not found"));

        return new ResDTO<>(
                "Friend request id fetched successfully",
                foundFriendRequest.getId(),
                HttpServletResponse.SC_OK
        );
    }

    public List<User> getFromUsersViaRequests(List<FriendRequest> friendRequests){
        return friendRequests.stream().map(FriendRequest::getFromUser).filter(User::isActive).toList();
    }

    public ResDTO<?> friendRequestAcceptation(FQAcceptationDTO request){
        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();

        FriendRequest fRequest =  friendRequestRepository.findById(request.getFriendReqId()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy lời mời kết bạn!")
        );

        if(!fRequest.getToUser().getId().equals(SecurityContextUtils.getUserId())){
            throw new BadRequestException("Bạn không có quyền!");
        }

        data.put("requestId", fRequest.getId());
        data.put("status", fRequest.getStatus().name());

        if(!fRequest.getStatus().equals(EFriendReqStatus.PENDING))
            throw new BadRequestException("Chỉ có thể xử lý các yêu cầu đang chờ!");

        if(request.getIsAccept()){
            acceptFriend(fRequest, data, response);
            friendRequestRepository.save(fRequest);

        }else {
            rejectFriendRequest(fRequest, data, response);
            friendRequestRepository.delete(fRequest);

        }

        return response;
    }

    @Override
    public ResDTO<?> getFriendSuggestions() {
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

        List<MinimizedUserResponse> responseData = suggestions.stream()
                .map(minimizedUserMapper::mapToDTO)
                .sorted(Comparator.comparing(MinimizedUserResponse::getUserFullName))
                .toList();

        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Friend suggestions fetched successfully!");

        return response;

    }


    private static void rejectFriendRequest(FriendRequest fRequest, Map<String, String> data, ResDTO<Map<String, String>> response) {
        fRequest.setStatus(EFriendReqStatus.DENIED);

        data.put("status", fRequest.getStatus().name());

        response.setData(data);
        response.setMessage("Đã từ chối lời mời kết bạn");
        response.setCode(HttpServletResponse.SC_OK);
    }

    private static void acceptFriend(FriendRequest fRequest, Map<String, String> data, ResDTO<Map<String, String>> response) {
        fRequest.setStatus(EFriendReqStatus.ACCEPTED);

        data.put("status", fRequest.getStatus().name());

        response.setData(data);
        response.setMessage("Đã chấp nhận lời mời kết bạn");
        response.setCode(HttpServletResponse.SC_OK);
    }

    private void sendFriendRequest(FriendRequest newRequest, HandleFriendRequestResponse responseData, ResDTO<HandleFriendRequestResponse> response){
        friendRequestRepository.save(newRequest);

        responseData.setRequestId(newRequest.getId());
        responseData.setStatus(newRequest.getStatus());

        FriendRequestMessage notification = getFriendRequestMessage(newRequest);

        kafkaEventPublisher.pubFriendRequestNoti(notification);

        response.setData(responseData);
        response.setMessage("Đã gửi lời mời kết bạn");
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

    private void unfriend(HandleFriendRequestResponse data, ResDTO<HandleFriendRequestResponse> response, User fromUser, User toUser){
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser).stream().filter(req -> req.isActive() && req.getStatus().equals(EFriendReqStatus.ACCEPTED)).toList();
        FriendRequest friendRequest = friendRequests.stream().findFirst().orElse(null);

        if(friendRequest == null){
            throw new BadRequestException("Không tìm thấy lời mời kết bạn");
        }

        friendRequestRepository.delete(friendRequest);
        data.setRequestId(friendRequest.getId());
        data.setStatus(EFriendReqStatus.CANCELLED);

        response.setData(data);
        response.setMessage("Đã hủy kết bạn");
        response.setCode(HttpServletResponse.SC_OK);
    }

    private void cancelFriendRequest(HandleFriendRequestResponse data, ResDTO<HandleFriendRequestResponse> response, User fromUser, User toUser){
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser).stream().filter(req -> req.isActive() && req.getStatus().equals(EFriendReqStatus.PENDING)).toList();
        FriendRequest friendRequest = friendRequests.stream().findFirst().orElse(null);

        if(friendRequest == null){
            throw new BadRequestException("Không tìm thấy lời mời kết bạn");
        }

        friendRequestRepository.delete(friendRequest);
        data.setRequestId(friendRequest.getId());
        data.setStatus(EFriendReqStatus.CANCELLED);

        response.setData(data);
        response.setMessage("Đã hủy gửi lời mời kết bạn");
        response.setCode(HttpServletResponse.SC_OK);
    }
}
