package vn.edu.tdtu.buddyblend.dto.response;

import java.io.Serializable;

public class MutualFriend implements Serializable {
    private String id;
    private String fullName;
    private String profileImage;

    public MutualFriend(String id, String fullName, String profileImage) {
        this.id = id;
        this.fullName = fullName;
        this.profileImage = profileImage;
    }

    public MutualFriend() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}