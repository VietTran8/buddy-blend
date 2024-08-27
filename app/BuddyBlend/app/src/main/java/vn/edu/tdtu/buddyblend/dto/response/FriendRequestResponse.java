package vn.edu.tdtu.buddyblend.dto.response;

import java.io.Serializable;

import vn.edu.tdtu.buddyblend.enums.EFriendReqStatus;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;

public class FriendRequestResponse implements Serializable {
    private String id;
    private EFriendReqStatus status;
    private String createdAt;
    private String updatedAt;
    private MinimizedUser fromUser;

    public FriendRequestResponse(String id, EFriendReqStatus status, String createdAt, String updatedAt, MinimizedUser fromUser) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fromUser = fromUser;
    }

    public FriendRequestResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EFriendReqStatus getStatus() {
        return status;
    }

    public void setStatus(EFriendReqStatus status) {
        this.status = status;
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

    public MinimizedUser getFromUser() {
        return fromUser;
    }

    public void setFromUser(MinimizedUser fromUser) {
        this.fromUser = fromUser;
    }
}