package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.MinimizedUserResponse;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MinimizedUserMapper {
    private final UserRepository userRepository;
    private final BaseUserMapper baseUserMapper;

    public MinimizedUserResponse mapToDTO(User user){
        if(user == null) {
           return null;
        }

        String userId = SecurityContextUtils.getUserId();
        User authUser = userRepository.findByIdAndActive(userId, true).orElse(null);

        MinimizedUserResponse minimizedUser = new MinimizedUserResponse(baseUserMapper.baseMapToDto(user));

        List<User> userFriends = baseUserMapper.getListFriends(user);
        List<User> myFriends = baseUserMapper.getListFriends(authUser);


        minimizedUser.setFriend(myFriends.stream().anyMatch(f -> !userId.equals(user.getId())
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
        minimizedUser.setFriendsCount(userFriends.size());

        return minimizedUser;
    }
}
