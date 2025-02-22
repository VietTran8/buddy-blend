package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.FQAcceptationDTO;
import vn.edu.tdtu.dto.request.FriendReqDTO;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;

import java.util.List;

public interface FriendRequestService {
    public ResDTO<?> handleFriendRequest(FriendReqDTO request);
    public List<User> getListFriends(String userId);
    public ResDTO<?> getListFriendsResp(String token, String id);
    public ResDTO<?> getFriendIds(String userId);
    public List<FriendRequest> getListFriendRequest(String userId);
    public ResDTO<?> getListFriendRequestResp(String token);
    public ResDTO<?> getFriendRequestIdByFromUserId(String fromUserId);
    public List<User> getFromUsersViaRequests(List<FriendRequest> friendRequests);
    public ResDTO<?> friendRequestAcceptation(FQAcceptationDTO request);
    public ResDTO<?> getFriendSuggestions();
}
