package vn.tdtu.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dtos.PaginationResponse;
import vn.tdtu.edu.dtos.ResDTO;
import vn.tdtu.edu.model.InteractNotification;
import vn.tdtu.edu.repository.NotificationRepository;
import vn.tdtu.edu.utils.JwtUtils;
import vn.tdtu.edu.utils.SecurityContextUtils;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractNotiService {
    private final NotificationRepository repository;
    private final JwtUtils jwtUtils;
    public void save(InteractNotification obj){
        repository.save(obj);
    }
    public ResDTO<PaginationResponse<InteractNotification>> findAllByToken(int page, int size) {
        String userId = SecurityContextUtils.getUserId();
        ResDTO<PaginationResponse<InteractNotification>> response = new ResDTO<>();

        Page<InteractNotification> notificationPage = repository.findByToUserId(userId, PageRequest.of(page - 1, size));

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
