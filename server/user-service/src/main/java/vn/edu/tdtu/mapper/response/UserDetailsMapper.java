package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.MutualFriend;
import vn.edu.tdtu.dto.response.UserDetailsResponse;
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
@Slf4j
public class UserDetailsMapper {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public UserDetailsResponse mapToDTO(User user){
        if(user == null) {
            return null;
        }

        String authUserId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(authUserId, true)
                .orElseThrow(() -> new BadRequestException("Người dùng không tìm thấy"));

        if(isBanned(authUser, user)) {
            return null;
        }

        List<User> myFriends = getListFriends(authUser);
        List<User> userFriends = getListFriends(user);
        List<FriendRequest> authUserFriendRequests = getUserFriendRequest(authUser, user);

        UserDetailsResponse userDetails = new UserDetailsResponse();

        userDetails.setFriend(myFriends.stream().anyMatch(f -> !authUserId.equals(user.getId())
                && f.getId().equals(user.getId())));

        userDetails.setFriendStatus(EFriendStatus.NOT_YET);

        authUserFriendRequests.stream().findFirst()
                .ifPresent(request -> {
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

        userDetails.setMyAccount(authUserId.equals(user.getId()));
        userDetails.setBio(user.getBio());
        userDetails.setCoverPicture(user.getCover());
        userDetails.setGender(user.getGender());
        userDetails.setPhone(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật...");
        userDetails.setFromCity(user.getFromCity() != null ? user.getFromCity() : "Chưa cập nhật...");

        List<MutualFriend> mutualFriends = getMutualFriends(myFriends, userFriends);

        userDetails.setMutualFriends(
                userDetails.isMyAccount() ?
                        new ArrayList<>() :
                        mutualFriends);

        userDetails.setOtherFriends(
                userDetails.isMyAccount() ?
                        getAllFriends(authUser) :
                        getAllFriends(user)
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

    private boolean isBanned(User authUser, User otherUser) {
        return !authUser.getId().equals(otherUser.getId()) &&
                (authUser.getBanningList().stream().anyMatch(b -> b.getBannedUser().getId().equals(otherUser.getId())) ||
                        otherUser.getBanningList().stream().anyMatch(b -> b.getBannedUser().getId().equals(authUser.getId())));
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

    private List<MutualFriend> getAllFriends(User user) {

        return getListFriends(user).stream()
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

        if(user != null){
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
