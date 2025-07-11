package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.FriendRequestRepository;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.UserStatusService;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.user.EFriendStatus;
import vn.tdtu.common.utils.SecurityContextUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BaseUserMapper {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    public UserDTO baseMapToDto(User user) {
        UserDTO userResponse = new UserDTO();

        String userId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(userId, true).orElse(null);

        List<FriendRequest> authUserFriendRequests = getUserFriendRequest(authUser, user);

        userResponse.setFriendStatus(EFriendStatus.NOT_YET);

        authUserFriendRequests.stream().findFirst()
                .ifPresent(request -> {
                    switch (request.getStatus()) {
                        case PENDING -> {
                            if (request.getToUser().getId().equals(user.getId())) {
                                userResponse.setFriendStatus(EFriendStatus.SENT_BY_YOU);
                            } else {
                                userResponse.setFriendStatus(EFriendStatus.SENT_TO_YOU);
                            }
                        }
                        case ACCEPTED -> {
                            userResponse.setFriendStatus(EFriendStatus.IS_FRIEND);
                        }
                        case CANCELLED -> {
                            userResponse.setFriendStatus(EFriendStatus.NOT_YET);
                        }
                    }
                });

        userResponse.setId(user.getId());
        userResponse.setUserFullName(user.getUserFullName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setMiddleName(user.getMiddleName());
        userResponse.setLastName(user.getLastName());
        userResponse.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "");
        userResponse.setNotificationKey(user.getNotificationKey());
        userResponse.setOnline(userStatusService.isUserOnline(user.getId()));

        return userResponse;
    }

    public List<UserDTO> getMutualFriends(List<User> myFriends, List<User> userFriends) {
        return myFriends.stream()
                .filter(userFriends::contains)
                .map(friend -> {
                    UserDTO mutualFriend = new UserDTO();

                    mutualFriend.setFullName(friend.getUserFullName());
                    mutualFriend.setId(friend.getId());
                    mutualFriend.setProfileImage(friend.getProfilePicture());

                    return mutualFriend;
                })
                .toList();
    }

    public List<User> getListFriends(User user) {
        List<User> friends = new ArrayList<>();

        if (user != null) {
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

    private List<FriendRequest> getUserFriendRequest(User fromUser, User toUser) {
        return friendRequestRepository.findByToUserAndFromUserOrFromUserAndToUser(toUser, fromUser, toUser, fromUser);
    }
}
