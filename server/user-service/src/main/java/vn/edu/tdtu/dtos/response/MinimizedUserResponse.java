package vn.edu.tdtu.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private boolean isFriend;
    private int friendsCount;
    private List<MutualFriend> mutualFriends;
}
