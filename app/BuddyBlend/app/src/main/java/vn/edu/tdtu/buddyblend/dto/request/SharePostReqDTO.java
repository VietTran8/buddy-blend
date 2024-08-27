package vn.edu.tdtu.buddyblend.dto.request;

import vn.edu.tdtu.buddyblend.enums.EPrivacy;

public class SharePostReqDTO {
    private String postId;
    private String status;
    private EPrivacy privacy;

    public SharePostReqDTO(String postId, String status, EPrivacy privacy) {
        this.postId = postId;
        this.status = status;
        this.privacy = privacy;
    }

    public SharePostReqDTO() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EPrivacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(EPrivacy privacy) {
        this.privacy = privacy;
    }
}
