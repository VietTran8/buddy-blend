package vn.tdtu.edu.message.newpost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.edu.dto.MutualFriend;
import vn.tdtu.edu.enums.EFriendStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserMessage {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private String createdAt;
    private String userFullName;
    private boolean isFriend;
    private EFriendStatus friendStatus;
    private boolean online;
    private int friendsCount;
    private List<MutualFriend> mutualFriends;
}
