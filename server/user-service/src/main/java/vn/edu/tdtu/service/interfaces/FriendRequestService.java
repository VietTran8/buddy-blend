package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.FQAcceptationDTO;
import vn.edu.tdtu.dto.request.FriendReqDTO;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

public interface FriendRequestService {
    ResponseVM<?> handleFriendRequest(FriendReqDTO request);

    List<User> getListFriends(String userId);

    ResponseVM<?> getListFriendsResp(String token, String id);

    ResponseVM<?> getFriendIds(String userId);

    List<FriendRequest> getListFriendRequest(String userId);

    ResponseVM<?> getListFriendRequestResp(String token);

    ResponseVM<?> getFriendRequestIdByFromUserId(String fromUserId);

    List<User> getFromUsersViaRequests(List<FriendRequest> friendRequests);

    ResponseVM<?> friendRequestAcceptation(FQAcceptationDTO request);

    ResponseVM<?> getFriendSuggestions();
}
