package vn.edu.tdtu.buddyblend.dto.request;

import java.util.List;

public class CreateCommentReqDTO {
    private String content;
    private String parentId;
    private List<String> imageUrls;
    private String postId;

    public CreateCommentReqDTO() {
    }

    public CreateCommentReqDTO(String content, String parentId, List<String> imageUrls, String postId) {
        this.content = content;
        this.parentId = parentId;
        this.imageUrls = imageUrls;
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "CreateCommentRequest{" +
                "content='" + content + '\'' +
                ", parentId='" + parentId + '\'' +
                ", imageUrls=" + imageUrls +
                ", postId='" + postId + '\'' +
                '}';
    }
}