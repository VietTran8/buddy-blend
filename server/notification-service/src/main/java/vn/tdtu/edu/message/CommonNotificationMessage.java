package vn.tdtu.edu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.common.enums.notification.ENotificationType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommonNotificationMessage {
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String refId;
    private String title;
    private String fromUserId;
    private List<String> toUserIds;
    private ENotificationType type;
    private String createAt;
}
