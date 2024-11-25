package vn.edu.tdtu.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EFriendStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private String id;
    private boolean isMyAccount;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String bio;
    private String phone;
    private String fromCity;
    private String profilePicture;
    private String coverPicture;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private String userFullName;
    private String notificationKey;
    private boolean isFriend;
    private int friendsCount;
    private EFriendStatus friendStatus;
    private List<MutualFriend> mutualFriends;
    private List<MutualFriend> otherFriends;
}
