package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.UserRepository;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDetailsMapper {
    private final UserRepository userRepository;
    private final BaseUserMapper baseUserMapper;

    public UserDTO mapToDTO(User user) {
        if (user == null) {
            return null;
        }

        String authUserId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(authUserId, true)
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND));

        if (isBanned(authUser, user)) {
            return null;
        }

        List<User> myFriends = baseUserMapper.getListFriends(authUser);
        List<User> userFriends = baseUserMapper.getListFriends(user);

        UserDTO userDetails = new UserDTO(baseUserMapper.baseMapToDto(user));

        userDetails.setFriend(myFriends.stream().anyMatch(f -> !authUserId.equals(user.getId())
                && f.getId().equals(user.getId())));
        userDetails.setMyAccount(authUserId.equals(user.getId()));
        userDetails.setBio(user.getBio());
        userDetails.setFriendsCount(userFriends.size());
        userDetails.setCoverPicture(user.getCover());
        userDetails.setGender(user.getGender());
        userDetails.setPhone(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật...");
        userDetails.setFromCity(user.getFromCity() != null ? user.getFromCity() : "Chưa cập nhật...");

        List<UserDTO> mutualFriends = baseUserMapper.getMutualFriends(myFriends, userFriends);

        userDetails.setMutualFriends(
                userDetails.isMyAccount() ?
                        new ArrayList<>() :
                        mutualFriends);

        userDetails.setOtherFriends(
                userDetails.isMyAccount() ?
                        getAllFriends(myFriends) :
                        getAllFriends(userFriends)
                                .stream()
                                .filter(friend -> !mutualFriends.contains(friend))
                                .toList()
        );

        return userDetails;
    }

    private boolean isBanned(User authUser, User otherUser) {
        return !authUser.getId().equals(otherUser.getId()) &&
                (authUser.getBlockingList().stream().anyMatch(b -> b.getBlockedUser().getId().equals(otherUser.getId())) ||
                        otherUser.getBlockingList().stream().anyMatch(b -> b.getBlockedUser().getId().equals(authUser.getId())));
    }

    private List<UserDTO> getAllFriends(List<User> friends) {
        return friends.stream()
                .map(friend -> {
                    UserDTO mutualFriend = new UserDTO();

                    mutualFriend.setFullName(friend.getUserFullName());
                    mutualFriend.setId(friend.getId());
                    mutualFriend.setProfileImage(friend.getProfilePicture());

                    return mutualFriend;
                })
                .toList();
    }
}
