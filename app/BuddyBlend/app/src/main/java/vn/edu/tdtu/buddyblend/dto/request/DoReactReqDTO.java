package vn.edu.tdtu.buddyblend.dto.request;

import vn.edu.tdtu.buddyblend.enums.EReactionType;

public class DoReactReqDTO {
    private String postId;
    private EReactionType type;

    public DoReactReqDTO() {
    }

    public DoReactReqDTO(String postId, EReactionType type) {
        this.postId = postId;
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public EReactionType getType() {
        return type;
    }

    public void setType(EReactionType type) {
        this.type = type;
    }
}
