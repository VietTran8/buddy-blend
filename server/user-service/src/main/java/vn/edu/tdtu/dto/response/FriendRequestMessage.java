package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestMessage {
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String notificationKey;
    private String title;
    private String toUserId;
}
