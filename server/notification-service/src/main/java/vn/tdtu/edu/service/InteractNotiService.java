package vn.tdtu.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dtos.ResDTO;
import vn.tdtu.edu.model.InteractNotification;
import vn.tdtu.edu.repository.NotificationRepository;
import vn.tdtu.edu.utils.JwtUtils;

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
    public ResDTO<List<InteractNotification>> findAllByToken(String token) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        ResDTO<List<InteractNotification>> response = new ResDTO<>();

        response.setCode(200);
        response.setData(repository.findAllByToUserId(userId)
                .stream()
                .sorted(Comparator.comparingLong((InteractNotification noti) -> Long.parseLong(noti.getCreateAt())).reversed())
                .toList());
        response.setMessage("success");

        return response;
    }

    public ResDTO<?> detachNotification(String token, String notificationId) {
        repository.deleteByIdAndToUserId(notificationId, jwtUtils.getUserIdFromJwtToken(token));

        return new ResDTO<>("success", null, 200);
    }
}
