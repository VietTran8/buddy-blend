package vn.edu.tdtu.buddyblend.dto.request;

public class FQAcceptationDTO {
    private String friendReqId;
    private Boolean isAccept;

    public FQAcceptationDTO(String friendReqId, Boolean isAccept) {
        this.friendReqId = friendReqId;
        this.isAccept = isAccept;
    }

    public String getFriendReqId() {
        return friendReqId;
    }

    public void setFriendReqId(String friendReqId) {
        this.friendReqId = friendReqId;
    }

    public Boolean getAccept() {
        return isAccept;
    }

    public void setAccept(Boolean accept) {
        isAccept = accept;
    }
}