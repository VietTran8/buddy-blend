package vn.edu.tdtu.buddyblend.dto.request;

import java.util.List;

public class SendMessage {
    private String content;
    private List<String> imageUrls;
    private String fromUserId;
    private String toUserId;

    public SendMessage(String content, List<String> imageUrls, String fromUserId, String toUserId) {
        this.content = content;
        this.imageUrls = imageUrls;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public SendMessage() {
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
