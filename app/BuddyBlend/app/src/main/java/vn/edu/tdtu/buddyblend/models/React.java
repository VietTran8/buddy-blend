package vn.edu.tdtu.buddyblend.models;

import vn.edu.tdtu.buddyblend.enums.EReactionType;

public class React {
    private String id;
    private EReactionType type;
    private String createdAt;
    private MinimizedUser user;
    private boolean isMine;

    public React() {
    }

    public React(String id, EReactionType type, String createdAt, MinimizedUser user, boolean isMine) {
        this.id = id;
        this.type = type;
        this.createdAt = createdAt;
        this.user = user;
        this.isMine = isMine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EReactionType getType() {
        return type;
    }

    public void setType(EReactionType type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public MinimizedUser getUser() {
        return user;
    }

    public void setUser(MinimizedUser minimizedUser) {
        this.user = minimizedUser;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}