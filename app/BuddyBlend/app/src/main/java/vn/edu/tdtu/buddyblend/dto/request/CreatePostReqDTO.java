package vn.edu.tdtu.buddyblend.dto.request;

import java.util.List;

import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;

public class CreatePostReqDTO {
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private EPrivacy privacy;
    private Boolean active;
    private EPostType type;
    private List<PostTagReqDTO> postTags;

    public CreatePostReqDTO() {
    }

    public CreatePostReqDTO(String content, List<String> imageUrls, List<String> videoUrls, EPrivacy privacy, Boolean active, EPostType type, List<PostTagReqDTO> postTags) {
        this.content = content;
        this.imageUrls = imageUrls;
        this.videoUrls = videoUrls;
        this.privacy = privacy;
        this.active = active;
        this.type = type;
        this.postTags = postTags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    public EPrivacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(EPrivacy privacy) {
        this.privacy = privacy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public EPostType getType() {
        return type;
    }

    public void setType(EPostType type) {
        this.type = type;
    }

    public List<PostTagReqDTO> getPostTags() {
        return postTags;
    }

    public void setPostTags(List<PostTagReqDTO> postTags) {
        this.postTags = postTags;
    }
}
