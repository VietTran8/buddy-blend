package vn.edu.tdtu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.dtos.response.MutualFriend;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
    private int friendsCount;
    private List<MutualFriend> mutualFriends;
}
