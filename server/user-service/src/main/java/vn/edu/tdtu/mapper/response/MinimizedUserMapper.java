package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.MinimizedUserResponse;
import vn.edu.tdtu.dtos.response.MutualFriend;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.models.FriendRequest;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.FriendRequestRepository;
import vn.edu.tdtu.repositories.UserRepository;
import vn.edu.tdtu.utils.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinimizedUserMapper {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    public MinimizedUserResponse mapToDTO(String token, User user){
        if(user != null) {
            MinimizedUserResponse minimizedUser = new MinimizedUserResponse();
            List<User> myFriends = new ArrayList<>();
            List<User> userFriends = getListFriends(user.getId());

            if(token != null && !token.isEmpty()) {
                String userId = jwtUtils.getUserIdFromJwtToken(token);
                myFriends = getListFriends(userId);

                minimizedUser.setFriend(myFriends.stream().anyMatch(f -> !userId.equals(user.getId())
                        && f.getId().equals(user.getId())));
            }

            minimizedUser.setMutualFriends(getMutualFriends(myFriends, userFriends));
            minimizedUser.setFriendsCount(userFriends.size());
            minimizedUser.setId(user.getId());
            minimizedUser.setUserFullName(user.getUserFullName());
            minimizedUser.setEmail(user.getEmail());
            minimizedUser.setCreatedAt(user.getCreatedAt());
            minimizedUser.setFirstName(user.getFirstName());
            minimizedUser.setMiddleName(user.getMiddleName());
            minimizedUser.setLastName(user.getLastName());
            minimizedUser.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "");
            minimizedUser.setNotificationKey(user.getNotificationKey());

            return minimizedUser;
        }

        return null;
    }

    private static List<MutualFriend> getMutualFriends(List<User> myFriends, List<User> userFriends) {

        return myFriends.stream()
                .filter(userFriends::contains)
                .map(friend -> {
                    MutualFriend mutualFriend = new MutualFriend();
                    mutualFriend.setFullName(friend.getUserFullName());
                    mutualFriend.setId(friend.getId());
                    mutualFriend.setProfileImage(friend.getProfilePicture());

                    return mutualFriend;
                })
                .toList();
    }

    private List<User> getListFriends(String userId) {
        User foundUser = userRepository.findByIdAndActive(userId, true).orElse(null);
        List<User> friends = new ArrayList<>();

        if(foundUser != null) {
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
}
