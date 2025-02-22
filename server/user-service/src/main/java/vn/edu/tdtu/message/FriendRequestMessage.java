package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestMessage {
    private String fromUserId;
    private String content;
    private String notificationKey;
    private String title;
    private String toUserId;
}
