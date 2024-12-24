package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.MinimizedUserResponse;
import vn.edu.tdtu.dto.response.MutualFriend;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.enums.EFriendStatus;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.FriendRequestRepository;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MinimizedUserMapper {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    public MinimizedUserResponse mapToDTO(User user){
        if(user == null) {
           return null;
        }

        MinimizedUserResponse minimizedUser = new MinimizedUserResponse();

        String userId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(userId, true).orElse(null);

        List<User> myFriends = getListFriends(authUser);
        List<User> userFriends = getListFriends(user);

        List<FriendRequest> authUserFriendRequests = getUserFriendRequest(authUser, user);

        minimizedUser.setFriendStatus(EFriendStatus.NOT_YET);

        authUserFriendRequests.stream().findFirst()
                .ifPresent(request -> {
                    switch (request.getStatus()) {
                        case PENDING -> {
                            if(request.getToUser().getId().equals(user.getId())){
                                minimizedUser.setFriendStatus(EFriendStatus.SENT_BY_YOU);
                            }else {
                                minimizedUser.setFriendStatus(EFriendStatus.SENT_TO_YOU);
                            }
                        }
                        case ACCEPTED -> {
                            minimizedUser.setFriendStatus(EFriendStatus.IS_FRIEND);
                        }
                        case CANCELLED -> {
                            minimizedUser.setFriendStatus(EFriendStatus.NOT_YET);
                        }
                    }
                });

        minimizedUser.setFriend(myFriends.stream().anyMatch(f -> !userId.equals(user.getId())
                && f.getId().equals(user.getId())));

        minimizedUser.setMutualFriends(getMutualFriends(myFriends, userFriends));
        minimizedUser.setFriendsCount(userFriends.size());
        minimizedUser.setId(user.getId());
        minimizedUser.setUserFullName(user.getUserFullName());
        minimizedUser.setEmail(user.getEmail());
        minimizedUser.setCreatedAt(user.getCreatedAt());
        minimizedUser.setFirstName(user.getFirstName());
        minimizedUser.setMiddleName(user.getMiddleName());
        minimizedUser.setLastName(user.getLastName());
        minimizedUser.setFirstThreeFriends(userFriends.stream().limit(3).map(friend -> friend.getProfilePicture() != null ? friend.getProfilePicture() : "").toList());
        minimizedUser.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "");
        minimizedUser.setNotificationKey(user.getNotificationKey());
        minimizedUser.setHiddenBanned(
                authUser != null && (authUser.getBanningList()
                        .stream()
                        .anyMatch(banning -> banning.getBannedUser().getId().equals(user.getId())) ||
                        user.getBanningList()
                                .stream()
                                .anyMatch(banning -> banning.getBannedUser().getId().equals(authUser.getId())))
        );

        return minimizedUser;
    }

    private List<FriendRequest> getUserFriendRequest(User fromUser, User toUser) {
        return friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser);
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

    private List<User> getListFriends(User user) {
        List<User> friends = new ArrayList<>();

        if(user != null) {
            List<FriendRequest> friendRequests = friendRequestRepository.findByFromUserAndStatusOrToUserAndStatus(user, EFriendReqStatus.ACCEPTED, user, EFriendReqStatus.ACCEPTED);
            friends = friendRequests.stream().map(request -> {
                if (request.getFromUser().getId().equals(user.getId())) {
                    return request.getToUser();
                }
                return request.getFromUser();
            }).filter(User::isActive).toList();
        }

        return friends;
    }
}
