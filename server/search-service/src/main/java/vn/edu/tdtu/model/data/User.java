package vn.edu.tdtu.model.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.dto.MutualFriend;
import vn.edu.tdtu.enums.EFriendStatus;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private String id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private String createdAt;
    private String userFullName;
    private String notificationKey;
    private boolean online;
    private boolean isFriend;
    private int friendsCount;
    private EFriendStatus friendStatus;
    private List<MutualFriend> mutualFriends;
}