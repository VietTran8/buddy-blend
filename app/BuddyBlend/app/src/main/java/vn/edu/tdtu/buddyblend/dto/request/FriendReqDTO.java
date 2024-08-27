package vn.edu.tdtu.buddyblend.dto.request;

public class FriendReqDTO {
    private String toUserId;

    public FriendReqDTO(String toUserId) {
        this.toUserId = toUserId;
    }

    public FriendReqDTO() {
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}