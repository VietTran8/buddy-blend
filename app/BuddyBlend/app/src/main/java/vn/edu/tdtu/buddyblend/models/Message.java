package vn.edu.tdtu.buddyblend.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import vn.edu.tdtu.buddyblend.dto.request.SendMessage;

public class Message {
    private String id;
    private String createdAt;
    private String content;
    private List<String> imageUrls;
    private String fromUserId;
    private String toUserId;
    private boolean read;
    private boolean sentByYou;

    public Message() {
    }

    public Message(String id, String createdAt, String content, List<String> imageUrls, String fromUserId, String toUserId, boolean read, boolean sentByYou) {
        this.id = id;
        this.createdAt = createdAt;
        this.content = content;
        this.imageUrls = imageUrls;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.read = read;
        this.sentByYou = sentByYou;
    }

    public Message (SendMessage sendMessage) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        this.id = "";
        this.createdAt = formatter.format(new Date());
        this.content = sendMessage.getContent();
        this.imageUrls = sendMessage.getImageUrls();
        this.fromUserId = sendMessage.getFromUserId();
        this.toUserId = sendMessage.getToUserId();
        this.read = false;
        this.sentByYou = true;
    }

    public boolean isSentByYou() {
        return sentByYou;
    }

    public void setSentByYou(boolean sentByYou) {
        this.sentByYou = sentByYou;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
