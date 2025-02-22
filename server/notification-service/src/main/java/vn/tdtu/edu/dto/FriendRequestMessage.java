package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FriendRequestMessage {
    private String fromUserId;
    private String content;
    private String notificationKey;
    private String toUserId;
    private String title;
}
