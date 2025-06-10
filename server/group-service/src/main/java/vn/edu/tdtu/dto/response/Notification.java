package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.notification.ENotificationType;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String refId;
    private String title;
    private String fromUserId;
    private List<String> toUserIds;
    private ENotificationType type;
    private Date createAt;
}
