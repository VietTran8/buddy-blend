package vn.edu.tdtu.buddyblend.dto.request;

public class PostTagReqDTO {
    private String taggedUserId;

    public String getTaggedUserId() {
        return taggedUserId;
    }

    public void setTaggedUserId(String taggedUserId) {
        this.taggedUserId = taggedUserId;
    }

    public PostTagReqDTO(String taggedUserId) {
        this.taggedUserId = taggedUserId;
    }

    public PostTagReqDTO() {
    }
}
