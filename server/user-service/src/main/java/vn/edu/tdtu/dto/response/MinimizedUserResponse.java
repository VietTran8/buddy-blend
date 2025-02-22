package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinimizedUserResponse extends BaseUserResponse {
    private List<String> firstThreeFriends;
    @JsonIgnore
    private boolean hiddenBanned;

    public MinimizedUserResponse(BaseUserResponse baseUser) {
        this.setId(baseUser.getId());
        this.setEmail(baseUser.getEmail());
        this.setFirstName(baseUser.getFirstName());
        this.setMiddleName(baseUser.getMiddleName());
        this.setLastName(baseUser.getLastName());
        this.setProfilePicture(baseUser.getProfilePicture());
        this.setCreatedAt(baseUser.getCreatedAt());
        this.setUserFullName(baseUser.getUserFullName());
        this.setNotificationKey(baseUser.getNotificationKey());
        this.setFriend(baseUser.isFriend());
        this.setMutualFriends(baseUser.getMutualFriends());
        this.setFriendsCount(baseUser.getFriendsCount());
        this.setOnline(baseUser.isOnline());
        this.setFriendStatus(baseUser.getFriendStatus());
    }
}
