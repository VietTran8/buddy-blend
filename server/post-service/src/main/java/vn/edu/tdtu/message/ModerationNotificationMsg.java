package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerationNotificationMsg extends ModerateImagesResultsMessage{
    private String toUserId;

    public ModerationNotificationMsg(ModerateImagesResultsMessage message, String toUserId) {
        this.toUserId = toUserId;
        this.setAccept(message.isAccept());
        this.setRejectReason(message.getRejectReason());
        this.setTimestamp(message.getTimestamp());
        this.setPostId(message.getPostId());
    }
}
