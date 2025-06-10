package vn.edu.tdtu.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EFriendStatus;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private String id;
    @JsonIgnore
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private String createdAt;
    private String userFullName;
    @JsonIgnore
    private String notificationKey;
    private boolean isFriend;
    private EFriendStatus friendStatus;
    private boolean online;
    private int friendsCount;
    private List<MutualFriend> mutualFriends;
}
