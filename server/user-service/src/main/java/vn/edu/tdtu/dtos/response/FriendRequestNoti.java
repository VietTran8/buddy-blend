package vn.edu.tdtu.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestNoti {
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String notificationKey;
    private String title;
}
