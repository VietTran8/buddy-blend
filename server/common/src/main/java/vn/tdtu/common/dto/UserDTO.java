package vn.tdtu.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.user.EFriendStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {

    @Schema(description = "Unique identifier of the user")
    private String id;

    @Schema(description = "Email address of the user")
    private String email;

    @Schema(description = "First name of the user")
    private String firstName;

    @Schema(description = "Middle name of the user")
    private String middleName;

    @Schema(description = "Last name of the user")
    private String lastName;

    @Schema(description = "URL of the user's profile picture")
    private String profilePicture;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Date and time when the user was created")
    private LocalDateTime createdAt;

    @Schema(description = "Full name of the user")
    private String userFullName;

    @Schema(description = "Notification key for the user")
    private String notificationKey;

    @Schema(description = "Indicates whether the user is a friend")
    private boolean isFriend;

    @Schema(description = "List of mutual friends")
    private List<UserDTO> mutualFriends;

    @Schema(description = "List of other friends")
    private List<UserDTO> otherFriends;

    @Schema(description = "Count of friends")
    private int friendsCount;

    @Schema(description = "Indicates whether the user is online")
    private boolean online;

    @Schema(description = "Friendship status with the user")
    private EFriendStatus friendStatus;

    @Schema(description = "List of the first three friends")
    private List<String> firstThreeFriends;

    @Schema(description = "Indicates whether the user is the current account")
    private boolean isMyAccount;

    @Schema(description = "Gender of the user")
    private String gender;

    @Schema(description = "Biography of the user")
    private String bio;

    @Schema(description = "Phone number of the user")
    private String phone;

    @Schema(description = "City the user is from")
    private String fromCity;

    @Schema(description = "URL of the user's cover picture")
    private String coverPicture;

    @JsonIgnore
    @Schema(description = "Indicates whether the user is banned ignore when serialized")
    private boolean hiddenBanned;

    @Schema(description = "Mutual friend full name")
    private String fullName;

    @Schema(description = "Mutual friend profile image")
    private String profileImage;

    public UserDTO(UserDTO baseUser) {
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