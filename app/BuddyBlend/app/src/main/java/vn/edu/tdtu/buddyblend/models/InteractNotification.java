package vn.edu.tdtu.buddyblend.models;

import java.util.Date;
import java.util.Map;

import vn.edu.tdtu.buddyblend.enums.ENotificationType;

public class InteractNotification {
    private String id;
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String title;
    private String postId;
    private String fromUserId;
    private String toUserId;
    private ENotificationType type;
    private String createAt;

    public InteractNotification(String id, String userFullName, String avatarUrl, String content, String title, String postId, String fromUserId, String toUserId, ENotificationType type, String createAt) {
        this.id = id;
        this.userFullName = userFullName;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.title = title;
        this.postId = postId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.type = type;
        this.createAt = createAt;
    }

    public InteractNotification(Map<String, String> data) {
        this.id = data.get("id");
        this.userFullName = data.get("userFullName");
        this.avatarUrl = data.get("avatarUrl");
        this.content = data.get("content");
        this.title = data.get("title");
        this.postId = data.get("postId");
        this.fromUserId = data.get("fromUserId");
        this.toUserId = data.get("toUserId");
        this.type = ENotificationType.valueOf(data.get("type"));
        this.createAt = data.get("createAt");
    }

    public InteractNotification() {
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public ENotificationType getType() {
        return type;
    }

    public void setType(ENotificationType type) {
        this.type = type;
    }
}
