package vn.edu.tdtu.buddyblend.dto.response;

import vn.edu.tdtu.buddyblend.enums.EFriendReqStatus;

public class HandleFriendRequestResponse {
    private String requestId;
    private EFriendReqStatus status;

    public HandleFriendRequestResponse(String requestId, EFriendReqStatus status) {
        this.requestId = requestId;
        this.status = status;
    }

    public HandleFriendRequestResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public EFriendReqStatus getStatus() {
        return status;
    }

    public void setStatus(EFriendReqStatus status) {
        this.status = status;
    }
}
