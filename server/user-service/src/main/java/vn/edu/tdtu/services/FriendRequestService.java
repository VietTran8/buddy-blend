package vn.edu.tdtu.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.FQAcceptationDTO;
import vn.edu.tdtu.dtos.request.FriendReqDTO;
import vn.edu.tdtu.dtos.response.FriendRequestNoti;
import vn.edu.tdtu.dtos.response.FriendRequestResponse;
import vn.edu.tdtu.dtos.response.HandleFriendRequestResponse;
import vn.edu.tdtu.dtos.response.MinimizedUserResponse;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.request.AddFriendReqMapper;
import vn.edu.tdtu.mapper.response.FriendRequestResponseMapper;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.models.FriendRequest;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.FriendRequestRepository;
import vn.edu.tdtu.repositories.UserRepository;
import vn.edu.tdtu.utils.JwtUtils;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final AddFriendReqMapper addFriendReqMapper;
    private final JwtUtils jwtUtils;
    private final SendKafkaMsgService kafkaMsgService;
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

    public List<User> getListFriends(String userId){
        User foundUser = userRepository.findByIdAndActive(userId, true).orElse(null);
        List<User> friends = new ArrayList<>();

        if(foundUser != null){
            List<FriendRequest> friendRequests = friendRequestRepository.findByFromUserAndStatusOrToUserAndStatus(foundUser, EFriendReqStatus.ACCEPTED, foundUser, EFriendReqStatus.ACCEPTED);
            friends = friendRequests.stream().map(request -> {
                if (request.getFromUser().getId().equals(userId)) {
                   return request.getToUser();
                }
                return request.getFromUser();
            }).filter(User::isActive).toList();
        }

        return friends;
    }

    public ResDTO<?> getListFriendsResp(String token, String id){
        ResDTO<List<MinimizedUserResponse>> response = new ResDTO<>();
        String userId = id == null ? SecurityContextUtils.getUserId() : id;

        List<MinimizedUserResponse> minimizedUsers = getListFriends(userId).stream().map(
                u -> minimizedUserMapper.mapToDTO(token, u)
        ).toList();

        log.info(userId);
        if(userId != null){
            response.setMessage("friends list fetched successfully");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(minimizedUsers);

            return response;
        }

        return JwtUtils.generateInvalidTokenResp();
    }

    public List<FriendRequest> getListFriendRequest(String userId){
        User foundUser = userRepository.findByIdAndActive(userId, true).orElse(null);
        List<FriendRequest> friendRequests = new ArrayList<>();

        if(foundUser != null){
            friendRequests = friendRequestRepository.findByFromUserAndStatusOrToUserAndStatus(foundUser, EFriendReqStatus.PENDING, foundUser, EFriendReqStatus.PENDING);
        }

        return friendRequests;
    }

    public ResDTO<?> getListFriendRequestResp(String token){
        ResDTO<List<FriendRequestResponse>> response = new ResDTO<>();
        String userId = SecurityContextUtils.getUserId();

        if(userId != null){
            response.setMessage("friend request list fetched successfully");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(
                    getListFriendRequest(userId)
                            .stream()
                            .filter(request -> request.getToUser().getId().equals(userId))
                            .map(request -> friendRequestResponseMapper.mapToDTO(token, request))
                            .toList()
            );

            return response;
        }

        return JwtUtils.generateInvalidTokenResp();
    }

    public List<User> getFromUsersViaRequests(List<FriendRequest> friendRequests){
        return friendRequests.stream().map(FriendRequest::getFromUser).filter(User::isActive).toList();
    }

    public ResDTO<?> friendRequestAcceptation(String token, FQAcceptationDTO request){
        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();
        friendRequestRepository.findById(request.getFriendReqId()).ifPresentOrElse(
                (fRequest) -> {
                    if(fRequest.getToUser().getId().equals(SecurityContextUtils.getUserId())){
                        data.put("requestId", fRequest.getId());
                        data.put("status", fRequest.getStatus().name());

                        if(fRequest.getStatus().equals(EFriendReqStatus.PENDING)){
                            if(request.getIsAccept()){
                                acceptFriend(fRequest, data, response);
                                friendRequestRepository.save(fRequest);
                            }else{
                                rejectFriendRequest(fRequest, data, response);
                                friendRequestRepository.delete(fRequest);
                            }
                        }else{
                            response.setData(data);
                            response.setMessage("Chỉ có thể xử lý các yêu cầu đang chờ!");
                            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                        }
                    }else {
                        response.setData(data);
                        response.setMessage("Bạn không có quyền!");
                        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                    }

                }, () -> {
                    response.setData(null);
                    response.setMessage("Không tìm thấy lời mời kết bạn");
                    response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                }
        );

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

    private boolean isSent(User fromUser, User toUser) {
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser);
        return friendRequests.stream()
                .anyMatch(req -> req.getStatus().equals(EFriendReqStatus.PENDING));
    }

    private void sendFriendRequest(FriendRequest newRequest, HandleFriendRequestResponse responseData, ResDTO<HandleFriendRequestResponse> response){
        friendRequestRepository.save(newRequest);

        responseData.setRequestId(newRequest.getId());
        responseData.setStatus(newRequest.getStatus());

        FriendRequestNoti notification = new FriendRequestNoti();
        notification.setAvatarUrl(newRequest.getFromUser().getProfilePicture());
        notification.setUserFullName(newRequest.getFromUser().getUserFullName());
        notification.setNotificationKey(newRequest.getToUser().getNotificationKey());
        notification.setContent(newRequest.getFromUser().getUserFullName() + " đã gửi cho bạn lời mời kết bạn");
        notification.setTitle("Yêu cầu kết bạn");

        kafkaMsgService.pubFriendRequestNoti(notification);

        response.setData(responseData);
        response.setMessage("Đã gửi lời mời kết bạn");
        response.setCode(HttpServletResponse.SC_OK);
    }

    private void unfriend(HandleFriendRequestResponse data, ResDTO<HandleFriendRequestResponse> response, User fromUser, User toUser){
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser).stream().filter(req -> req.isActive() && req.getStatus().equals(EFriendReqStatus.ACCEPTED)).toList();
        FriendRequest friendRequest = friendRequests.stream().findFirst().orElse(null);
        if(friendRequest != null){
            friendRequestRepository.delete(friendRequest);
            data.setRequestId(friendRequest.getId());
            data.setStatus(EFriendReqStatus.CANCELLED);

            response.setData(data);
            response.setMessage("Đã hủy kết bạn");
            response.setCode(HttpServletResponse.SC_OK);
        }else{
            response.setData(null);
            response.setMessage("Lỗi xảy ra");
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void cancelFriendRequest(HandleFriendRequestResponse data, ResDTO<HandleFriendRequestResponse> response, User fromUser, User toUser){
        List<FriendRequest> friendRequests = friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser).stream().filter(req -> req.isActive() && req.getStatus().equals(EFriendReqStatus.PENDING)).toList();
        FriendRequest friendRequest = friendRequests.stream().findFirst().orElse(null);
        if(friendRequest != null){
            friendRequestRepository.delete(friendRequest);
            data.setRequestId(friendRequest.getId());
            data.setStatus(EFriendReqStatus.CANCELLED);

            response.setData(data);
            response.setMessage("Đã hủy gửi lời mời kết bạn");
            response.setCode(HttpServletResponse.SC_OK);
        }else{
            response.setData(null);
            response.setMessage("Lỗi xảy ra");
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
