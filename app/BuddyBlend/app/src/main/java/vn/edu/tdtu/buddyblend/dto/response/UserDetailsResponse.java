package vn.edu.tdtu.buddyblend.dto.response;

import java.util.List;

import vn.edu.tdtu.buddyblend.enums.EFriendStatus;

public class UserDetailsResponse {
    private String id;
    private boolean myAccount;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String bio;
    private String profilePicture;
    private String coverPicture;
    private String createdAt;
    private String userFullName;
    private String notificationKey;
    private boolean friend;
    private int friendsCount;
    private EFriendStatus friendStatus;
    private List<MutualFriend> mutualFriends;
    private List<MutualFriend> otherFriends;

    public UserDetailsResponse(String id, boolean isMyAccount, String email, String firstName, String middleName, String lastName, String gender, String bio, String profilePicture, String coverPicture, String createdAt, String userFullName, String notificationKey, boolean isFriend, int friendsCount, EFriendStatus friendStatus, List<MutualFriend> mutualFriends, List<MutualFriend> otherFriends) {
        this.id = id;
        this.myAccount = isMyAccount;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.coverPicture = coverPicture;
        this.createdAt = createdAt;
        this.userFullName = userFullName;
        this.notificationKey = notificationKey;
        this.friend = isFriend;
        this.friendsCount = friendsCount;
        this.friendStatus = friendStatus;
        this.mutualFriends = mutualFriends;
        this.otherFriends = otherFriends;
    }

    public UserDetailsResponse() {
    }

    public EFriendStatus getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(EFriendStatus friendStatus) {
        this.friendStatus = friendStatus;
    }

    public boolean isMyAccount() {
        return myAccount;
    }

    public void setMyAccount(boolean myAccount) {
        myAccount = myAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
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

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public List<MutualFriend> getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(List<MutualFriend> mutualFriends) {
        this.mutualFriends = mutualFriends;
    }

    public List<MutualFriend> getOtherFriends() {
        return otherFriends;
    }

    public void setOtherFriends(List<MutualFriend> otherFriends) {
        this.otherFriends = otherFriends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}