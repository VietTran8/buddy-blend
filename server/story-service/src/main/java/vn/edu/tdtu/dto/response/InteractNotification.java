package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.ENotificationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractNotification {
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String postId;
    private String title;
    private String fromUserId;
    private String toUserId;
    private ENotificationType type;
    private String createAt;
}