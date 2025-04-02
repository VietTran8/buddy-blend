package vn.edu.tdtu.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EFriendStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseUserResponse {
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
    private boolean isFriend;
    private List<MutualFriend> mutualFriends;
    private int friendsCount;
    private boolean online;
    private EFriendStatus friendStatus;
}
