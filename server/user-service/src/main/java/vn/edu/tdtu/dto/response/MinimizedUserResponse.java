package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EFriendStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinimizedUserResponse {
    private String id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private String userFullName;
    private String notificationKey;
    private List<String> firstThreeFriends;
    private boolean isFriend;
    private int friendsCount;
    private List<MutualFriend> mutualFriends;
    private EFriendStatus friendStatus;
    @JsonIgnore
    private boolean hiddenBanned;
}
