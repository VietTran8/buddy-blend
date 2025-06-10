package vn.tdtu.edu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.tdtu.common.enums.notification.ENotificationType;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommonNotification {
    @Id
    @Indexed
    private String id;
    private String content;
    private String title;
    private String refId;
    private String fromUserId;
    private List<UserInfo> toUsers;
    private ENotificationType type;
    private String createAt;
}