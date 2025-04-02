package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import vn.tdtu.edu.enums.ENotificationType;
import vn.tdtu.edu.model.data.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationResponse {
    private String id;
//    private String userFullName;
//    private String avatarUrl;
//    private String fromUserId;
    private User fromUser;
    private String content;
    private String title;
    private String refId;
//    private List<String> toUserIds;
    private ENotificationType type;
    private String toUserId;
    private String createAt;
    private boolean hasRead;
}

