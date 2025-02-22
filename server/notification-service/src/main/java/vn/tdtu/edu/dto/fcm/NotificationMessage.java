package vn.tdtu.edu.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationMessage<D> {
    private String token;
    private D data;
    private NotificationContent notification;
}
