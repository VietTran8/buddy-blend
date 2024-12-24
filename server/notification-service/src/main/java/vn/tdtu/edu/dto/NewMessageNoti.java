package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMessageNoti extends Message{
    private String title;
    private String avatar;

    public NewMessageNoti(Message message, String title, String avatar) {
        this.setId(message.getId());
        this.setTitle(title);
        this.setAvatar(avatar);
        this.setContent(message.getContent());
        this.setCreatedAt(message.getCreatedAt());
        this.setImageUrls(message.getImageUrls());
        this.setFromUserId(message.getFromUserId());
        this.setToUserId(message.getToUserId());
    }
}
