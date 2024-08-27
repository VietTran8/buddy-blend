package vn.edu.tdtu.buddyblend.models;

import java.util.List;

public class Comment {
    private String id;
    private String content;
    private String parentId;
    private List<String> imageUrls;
    private String createdAt;
    private String updatedAt;
    private MinimizedUser user;
    private List<Comment> children;
    private boolean isMine;

    public Comment() {
    }

    public Comment(String id, String content, String parentId, List<String> imageUrls, String createdAt, String updatedAt, MinimizedUser minimizedUser, List<Comment> children, boolean isMine) {
        this.id = id;
        this.content = content;
        this.parentId = parentId;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = minimizedUser;
        this.children = children;
        this.isMine = isMine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public MinimizedUser getUser() {
        return user;
    }

    public void setUser(MinimizedUser minimizedUser) {
        this.user = minimizedUser;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
