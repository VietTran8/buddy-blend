package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerationNotificationMsg extends ModerateResultsMessage {
    private String toUserId;

    public ModerationNotificationMsg(ModerateResultsMessage message, String toUserId) {
        this.toUserId = toUserId;
        this.setAccept(message.isAccept());
        this.setRejectReason(message.getRejectReason());
        this.setTimestamp(message.getTimestamp());
        this.setRefId(message.getRefId());
        this.setType(message.getType());
    }
}
