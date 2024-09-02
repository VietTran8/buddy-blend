package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.MutualFriend;
import vn.edu.tdtu.dtos.response.UserDetailsResponse;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.enums.EFriendStatus;
import vn.edu.tdtu.models.FriendRequest;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.FriendRequestRepository;
import vn.edu.tdtu.repositories.UserRepository;
import vn.edu.tdtu.utils.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDetailsMapper {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public UserDetailsResponse mapToDTO(String token, User user){
        if(user != null) {
            UserDetailsResponse userDetails = new UserDetailsResponse();
            List<User> myFriends = new ArrayList<>();
            List<User> userFriends = getListFriends(user.getId());

            userDetails.setMyAccount(false);
            userDetails.setFriendStatus(EFriendStatus.NOT_YET);

            if(token != null && !token.isEmpty() ) {
                String userId = jwtUtils.getUserIdFromJwtToken(token);
                myFriends = getListFriends(userId);

                userDetails.setFriend(myFriends.stream().anyMatch(f -> !userId.equals(user.getId())
                        && f.getId().equals(user.getId())));

                userDetails.setMyAccount(userId.equals(user.getId()));

                userRepository.findByIdAndActive(userId, true)
                        .ifPresent(currentUser -> {
                            List<FriendRequest> friendRequests = getUserFriendRequest(currentUser, user);

                            log.info("currentUser id: " + currentUser.getId());
                            log.info("targetUser id: " + user.getId());

                            log.info(friendRequests.size() + "");

                            friendRequests.stream().findFirst()
                                    .ifPresent(request -> {
                                        log.info(request.getStatus().name());

                                        switch (request.getStatus()) {
                                            case PENDING -> {
                                                if(request.getToUser().getId().equals(user.getId())){
                                                    userDetails.setFriendStatus(EFriendStatus.SENT_BY_YOU);
                                                }else {
                                                    userDetails.setFriendStatus(EFriendStatus.SENT_TO_YOU);
                                                }
                                            }
                                            case ACCEPTED -> {
                                                userDetails.setFriendStatus(EFriendStatus.IS_FRIEND);
                                            }
                                            case CANCELLED -> {
                                                userDetails.setFriendStatus(EFriendStatus.NOT_YET);
                                            }
                                        }
                                    });
                        });
            }

            userDetails.setBio(user.getBio());
            userDetails.setCoverPicture(user.getCover());
            userDetails.setGender(user.getGender());

            List<MutualFriend> mutualFriends = getMutualFriends(myFriends, userFriends);

            userDetails.setMutualFriends(
                    userDetails.isMyAccount() ?
                    new ArrayList<>() :
                    mutualFriends);

            userDetails.setOtherFriends(
                    userDetails.isMyAccount() ?
                    getAllFriends(jwtUtils.getUserIdFromJwtToken(token)) :
                    getAllFriends(user.getId())
                            .stream()
                            .filter(friend -> !mutualFriends.contains(friend))
                            .toList()
            );

            userDetails.setFriendsCount(userFriends.size());
            userDetails.setId(user.getId());
            userDetails.setUserFullName(user.getUserFullName());
            userDetails.setEmail(user.getEmail());
            userDetails.setCreatedAt(user.getCreatedAt());
            userDetails.setFirstName(user.getFirstName());
            userDetails.setMiddleName(user.getMiddleName());
            userDetails.setLastName(user.getLastName());
            userDetails.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "");
            userDetails.setNotificationKey(user.getNotificationKey());

            return userDetails;
        }

        return null;
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

    private List<MutualFriend> getAllFriends(String userId) {

        return getListFriends(userId).stream()
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
}
