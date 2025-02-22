package vn.tdtu.edu.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestBody<D> {
    private NotificationMessage<D> message;
}
