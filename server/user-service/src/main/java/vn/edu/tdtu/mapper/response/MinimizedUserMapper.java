package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.util.SecurityContextUtils;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.user.EUserMappingType;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinimizedUserMapper {
    private final UserRepository userRepository;
    private final BaseUserMapper baseUserMapper;

    public UserDTO mapToDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO minimizedUser = new UserDTO(baseUserMapper.baseMapToDto(user), EUserMappingType.TYPE_MINIMIZED);

        String authUserId = SecurityContextUtils.getUserId();

        if (authUserId == null)
            return minimizedUser;

        User authUser = userRepository.findByIdAndActive(authUserId, true).orElse(null);

        List<User> userFriends = baseUserMapper.getListFriends(user);
        List<User> myFriends = baseUserMapper.getListFriends(authUser);

        minimizedUser.setFriend(myFriends.stream().anyMatch(f -> !authUserId.equals(user.getId())
                && f.getId().equals(user.getId())));
        minimizedUser.setFirstThreeFriends(userFriends.stream().limit(3).map(friend -> friend.getProfilePicture() != null ? friend.getProfilePicture() : "").toList());
        minimizedUser.setHiddenBanned(
                authUser != null && (authUser.getBlockingList()
                        .stream()
                        .anyMatch(banning -> banning.getBlockedUser().getId().equals(user.getId())) ||
                        user.getBlockingList()
                                .stream()
                                .anyMatch(banning -> banning.getBlockedUser().getId().equals(authUser.getId())))
        );
        minimizedUser.setMutualFriends(baseUserMapper.getMutualFriends(myFriends, userFriends));

        return minimizedUser;
    }
}
