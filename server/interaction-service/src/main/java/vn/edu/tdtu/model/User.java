package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EFriendStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private String createdAt;
    private String userFullName;
    private String notificationKey;
    private EFriendStatus friendStatus;
    private boolean isFriend;
    private int friendsCount;
}
