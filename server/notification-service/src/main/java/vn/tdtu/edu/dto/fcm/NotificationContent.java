package vn.tdtu.edu.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationContent {
    private String title;
    private String body;
    private String image;
}
