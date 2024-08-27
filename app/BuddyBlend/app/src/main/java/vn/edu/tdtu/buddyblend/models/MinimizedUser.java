package vn.edu.tdtu.buddyblend.models;

import java.io.Serializable;
import java.util.List;

import vn.edu.tdtu.buddyblend.dto.response.MutualFriend;

public class MinimizedUser implements Serializable {
    private String id;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private String createdAt;
    private String userFullName;
    private String notificationKey;
    private boolean friend;
    private int friendsCount;
    private List<MutualFriend> mutualFriends;

    public MinimizedUser(String id, String email, String firstName, String middleName, String lastName, String profilePicture, String createdAt, String userFullName, String notificationKey, boolean friend, int friendsCount, List<vn.edu.tdtu.buddyblend.dto.response.MutualFriend> mutualFriends) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.createdAt = createdAt;
        this.userFullName = userFullName;
        this.notificationKey = notificationKey;
        this.friend = friend;
        this.friendsCount = friendsCount;
        this.mutualFriends = mutualFriends;
    }

    public List<vn.edu.tdtu.buddyblend.dto.response.MutualFriend> getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(List<vn.edu.tdtu.buddyblend.dto.response.MutualFriend> mutualFriends) {
        this.mutualFriends = mutualFriends;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", notificationKey='" + notificationKey + '\'' +
                '}';
    }
}
