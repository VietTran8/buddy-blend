package vn.tdtu.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.PaginationResponse;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.repository.NotificationRepository;
import vn.tdtu.edu.service.interfaces.NotificationService;
import vn.tdtu.edu.util.SecurityContextUtils;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;

    public void save(CommonNotification obj){
        repository.save(obj);
    }
    public ResDTO<PaginationResponse<CommonNotification>> findAllUserNotifications(int page, int size) {
        String userId = SecurityContextUtils.getUserId();
        ResDTO<PaginationResponse<CommonNotification>> response = new ResDTO<>();

        Page<CommonNotification> notificationPage = repository.findByToUserId(userId, PageRequest.of(page - 1, size));

        response.setCode(200);
        response.setData(new PaginationResponse<>(
                page,
                size,
                notificationPage.getTotalPages(),
                notificationPage.get().toList(),
                notificationPage.getTotalElements()
        ));
        response.setMessage("success");

        return response;
    }
    public ResDTO<?> detachNotification(String notificationId) {
        repository.deleteByIdAndToUserIdsContaining(notificationId, SecurityContextUtils.getUserId());

        return new ResDTO<>("success", null, 200);
    }

}