package vn.edu.tdtu.buddyblend.models;

import java.util.List;

public class Room {
    private String id;
    private String userId1;
    private String userId2;
    private String createdAt;
    private List<Message> messages;
    private Message latestMessage;
    private String roomName;
    private String roomImage;
    private String opponentUserId;

    public Room() {
    }

    public Room(String id, String userId1, String userId2, String createdAt, List<Message> messages, Message latestMessage, String roomName, String roomImage, String opponentUserId) {
        this.id = id;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.createdAt = createdAt;
        this.messages = messages;
        this.latestMessage = latestMessage;
        this.roomName = roomName;
        this.roomImage = roomImage;
        this.opponentUserId = opponentUserId;
    }

    public String getOpponentUserId() {
        return opponentUserId;
    }

    public void setOpponentUserId(String opponentUserId) {
        this.opponentUserId = opponentUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }
}
