package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.notification.ENotificationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationResponse {
    private String id;
    private UserDTO fromUser;
    private String content;
    private String title;
    private String refId;
    private ENotificationType type;
    private String toUserId;
    private String createAt;
    private boolean hasRead;
}

