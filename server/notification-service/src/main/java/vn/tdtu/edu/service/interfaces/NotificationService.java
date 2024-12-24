package vn.tdtu.edu.service.interfaces;

import vn.tdtu.edu.dto.PaginationResponse;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.model.CommonNotification;

public interface NotificationService {
    public void save(CommonNotification obj);
    public ResDTO<PaginationResponse<CommonNotification>> findAllUserNotifications(int page, int size);
    public ResDTO<?> detachNotification(String notificationId);
}
