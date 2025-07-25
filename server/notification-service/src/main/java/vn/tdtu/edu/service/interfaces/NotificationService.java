package vn.tdtu.edu.service.interfaces;

import vn.tdtu.common.viewmodel.PaginationResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.dto.NotificationResponse;
import vn.tdtu.edu.model.CommonNotification;

public interface NotificationService {
    void save(CommonNotification obj);

    ResponseVM<PaginationResponseVM<NotificationResponse>> findAllUserNotifications(int page, int size);

    ResponseVM<?> detachNotification(String notificationId);

    ResponseVM<?> testGetResource();

    ResponseVM<?> readNotification(String notificationId);
}
