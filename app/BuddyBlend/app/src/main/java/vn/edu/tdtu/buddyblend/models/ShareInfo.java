package vn.edu.tdtu.buddyblend.models;

import java.io.Serializable;

import vn.edu.tdtu.buddyblend.enums.EPrivacy;

public class ShareInfo implements Serializable {
    private String id;
    private String status;
    private String sharedAt;

    private EPrivacy privacy;
    private MinimizedUser sharedUser;

    public ShareInfo(String id, String status, String sharedAt, MinimizedUser sharedUser) {
        this.id = id;
        this.status = status;
        this.sharedAt = sharedAt;
        this.sharedUser = sharedUser;
    }

    public ShareInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EPrivacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(EPrivacy privacy) {
        this.privacy = privacy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(String sharedAt) {
        this.sharedAt = sharedAt;
    }

    public MinimizedUser getSharedUser() {
        return sharedUser;
    }

    public void setSharedUser(MinimizedUser sharedUser) {
        this.sharedUser = sharedUser;
    }
}