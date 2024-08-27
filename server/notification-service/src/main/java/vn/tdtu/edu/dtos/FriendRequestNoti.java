package vn.tdtu.edu.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FriendRequestNoti {
    private String userFullName;
    private String avatarUrl;
    private String content;
    private String notificationKey;
    private String title;
}
