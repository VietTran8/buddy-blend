package vn.edu.tdtu.buddyblend.models;

import java.io.Serializable;
import java.util.List;

import vn.edu.tdtu.buddyblend.enums.EPostType;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.enums.EReactionType;

public class PostResponse implements Serializable {
    private String id;
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private String createdAt;
    private String updatedAt;
    private EPrivacy privacy;
    private EPostType type;
    private MinimizedUser user;
    private int noShared;
    private EReactionType reacted;
    private int noComments;
    private int noReactions;
    private List<TopReacts> topReacts;
    private ShareInfo shareInfo;
    private List<MinimizedUser> taggedUsers;
    private boolean mine;

    public PostResponse() {
    }

    public PostResponse(String id, String content, List<String> imageUrls, List<String> videoUrls, String createdAt, String updatedAt, EPrivacy privacy, EPostType type, MinimizedUser minimizedUser, int noShared, EReactionType reacted, int noComments, int noReactions, List<TopReacts> topReacts, ShareInfo shareInfo, List<MinimizedUser> taggedMinimizedUsers, boolean mine) {
        this.id = id;
        this.content = content;
        this.imageUrls = imageUrls;
        this.videoUrls = videoUrls;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.privacy = privacy;
        this.type = type;
        this.user = minimizedUser;
        this.noShared = noShared;
        this.reacted = reacted;
        this.noComments = noComments;
        this.noReactions = noReactions;
        this.topReacts = topReacts;
        this.shareInfo = shareInfo;
        this.taggedUsers = taggedMinimizedUsers;
        this.mine = mine;
    }

    public List<TopReacts> getTopReacts() {
        return topReacts;
    }

    public void setTopReacts(List<TopReacts> topReacts) {
        this.topReacts = topReacts;
    }

    public EReactionType getReacted() {
        return reacted;
    }
    public void setReacted(EReactionType reacted) {
        this.reacted = reacted;
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
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public EPrivacy getPrivacy() {
        return privacy;
    }
    public void setPrivacy(EPrivacy privacy) {
        this.privacy = privacy;
    }
    public EPostType getType() {
        return type;
    }
    public void setType(EPostType type) {
        this.type = type;
    }
    public MinimizedUser getUser() {
        return user;
    }
    public void setUser(MinimizedUser minimizedUser) {
        this.user = minimizedUser;
    }
    public int getNoShared() {
        return noShared;
    }
    public void setNoShared(int noShared) {
        this.noShared = noShared;
    }
    public int getNoComments() {
        return noComments;
    }
    public void setNoComments(int noComments) {
        this.noComments = noComments;
    }
    public int getNoReactions() {
        return noReactions;
    }
    public void setNoReactions(int noReactions) {
        this.noReactions = noReactions;
    }
    public ShareInfo getShareInfo() {
        return shareInfo;
    }
    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }
    public List<MinimizedUser> getTaggedUsers() {
        return taggedUsers;
    }
    public void setTaggedUsers(List<MinimizedUser> taggedMinimizedUsers) {
        this.taggedUsers = taggedMinimizedUsers;
    }
    public boolean isMine() {
        return mine;
    }
    public void setMine(boolean mine) {
        this.mine = mine;
    }
    public String getUserFullName(){
        return String.join(" ", this.getUser().getFirstName(), this.getUser().getMiddleName(), this.getUser().getLastName());
    }

}