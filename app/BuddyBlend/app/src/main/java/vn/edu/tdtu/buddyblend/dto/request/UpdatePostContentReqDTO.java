package vn.edu.tdtu.buddyblend.dto.request;

import java.util.List;

import vn.edu.tdtu.buddyblend.enums.EPrivacy;

public class UpdatePostContentReqDTO {
    private String id;
    private String content;
    private EPrivacy privacy;
    private List<String> taggingUsers;
    private List<String> videoUrls;
    private List<String> imageUrls;

    public UpdatePostContentReqDTO(String id, String content, EPrivacy privacy, List<String> taggingUsers, List<String> videoUrls, List<String> imageUrls) {
        this.id = id;
        this.content = content;
        this.privacy = privacy;
        this.taggingUsers = taggingUsers;
        this.videoUrls = videoUrls;
        this.imageUrls = imageUrls;
    }

    public UpdatePostContentReqDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EPrivacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(EPrivacy privacy) {
        this.privacy = privacy;
    }

    public List<String> getTaggingUsers() {
        return taggingUsers;
    }

    public void setTaggingUsers(List<String> taggingUsers) {
        this.taggingUsers = taggingUsers;
    }

    public List<String> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}