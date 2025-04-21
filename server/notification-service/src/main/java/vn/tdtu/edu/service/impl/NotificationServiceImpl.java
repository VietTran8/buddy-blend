package vn.tdtu.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.tdtu.edu.dto.NotificationResponse;
import vn.tdtu.edu.dto.PaginationResponse;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.exception.BadRequestException;
import vn.tdtu.edu.mapper.NotificationMapper;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.model.data.User;
import vn.tdtu.edu.repository.NotificationRepository;
import vn.tdtu.edu.service.interfaces.NotificationService;
import vn.tdtu.edu.service.interfaces.UserService;
import vn.tdtu.edu.util.SecurityContextUtils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    @Override
    public void save(CommonNotification obj){
        repository.save(obj);
    }

    @Override
    public ResDTO<PaginationResponse<NotificationResponse>> findAllUserNotifications(String tokenHeader, int page, int size) {
        String userId = SecurityContextUtils.getUserId();
        ResDTO<PaginationResponse<NotificationResponse>> response = new ResDTO<>();

        Page<CommonNotification> notificationPage = repository.findByToUserId(userId, PageRequest.of(page - 1, size));

        Set<String> fromUserIdSet = notificationPage.get()
                .map(CommonNotification::getFromUserId)
                .collect(Collectors.toSet());

        Map<String, User> fromUserMap = userService.findByIds(fromUserIdSet.stream().toList())
                .stream().collect(Collectors.toMap(
                        User::getId, user -> user
                ));

        response.setCode(200);
        response.setData(new PaginationResponse<>(
                page,
                size,
                notificationPage.getTotalPages(),
                notificationPage.get().map(noti -> notificationMapper.mapToDto(noti, fromUserMap)).toList(),
                notificationPage.getTotalElements()
        ));
        response.setMessage("success");

        return response;
    }

    @Override
    public ResDTO<?> detachNotification(String tokenHeader, String notificationId) {
        CommonNotification notification = repository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException("Notification not found!"));

        if(notification.getToUsers().stream().noneMatch(
                user -> SecurityContextUtils.getUserId().equals(user.getUserId())
        ))
            throw new BadRequestException("You are not the notification owner");

        repository.deleteById(notificationId);

        return new ResDTO<>("success", null, 200);
    }

    @Override
    public ResDTO<?> readNotification(String tokenHeader, String notificationId) {
        CommonNotification notification = repository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException("Notification not found!"));

        if(notification.getToUsers().stream().noneMatch(
                user -> SecurityContextUtils.getUserId().equals(user.getUserId())
        ))
            throw new BadRequestException("You are not the notification owner");

        notification.setToUsers(
                notification.getToUsers().stream().peek(
                        user -> {
                            if(SecurityContextUtils.getUserId().equals(user.getUserId()))
                                user.setHasRead(true);
                        }
                ).toList()
        );

        repository.save(notification);

        return new ResDTO<>("success", null, 200);
    }
}