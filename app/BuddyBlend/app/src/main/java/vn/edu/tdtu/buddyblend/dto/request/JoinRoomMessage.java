package vn.edu.tdtu.buddyblend.dto.request;

public class JoinRoomMessage {
    private String fromUserId;
    private String toUserId;

    public JoinRoomMessage(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public JoinRoomMessage() {
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
}
