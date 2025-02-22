package vn.tdtu.edu.service.interfaces;

import vn.tdtu.edu.dto.NotificationResponse;
import vn.tdtu.edu.dto.PaginationResponse;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.model.CommonNotification;

public interface NotificationService {
    public void save(CommonNotification obj);
    public ResDTO<PaginationResponse<NotificationResponse>> findAllUserNotifications(int page, int size);
    public ResDTO<?> detachNotification(String notificationId);
    public ResDTO<?> readNotification(String notificationId);
}
