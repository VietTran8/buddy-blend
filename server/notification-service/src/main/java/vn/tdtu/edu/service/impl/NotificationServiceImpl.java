package vn.tdtu.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.PaginationResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.dto.NotificationResponse;
import vn.tdtu.edu.mapper.NotificationMapper;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.repository.NotificationRepository;
import vn.tdtu.edu.service.interfaces.NotificationService;
import vn.tdtu.edu.service.interfaces.UserService;

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
    public void save(CommonNotification obj) {
        repository.save(obj);
    }

    @Override
    public ResponseVM<PaginationResponseVM<NotificationResponse>> findAllUserNotifications(String tokenHeader, int page, int size) {
        String userId = SecurityContextUtils.getUserId();
        ResponseVM<PaginationResponseVM<NotificationResponse>> response = new ResponseVM<>();

        Page<CommonNotification> notificationPage = repository.findByToUserId(userId, PageRequest.of(page - 1, size));

        Set<String> fromUserIdSet = notificationPage.get()
                .map(CommonNotification::getFromUserId)
                .collect(Collectors.toSet());

        Map<String, UserDTO> fromUserMap = userService.findByIds(fromUserIdSet.stream().toList())
                .stream().collect(Collectors.toMap(
                        UserDTO::getId, user -> user
                ));

        response.setCode(200);
        response.setData(new PaginationResponseVM<>(
                page,
                size,
                notificationPage.getTotalPages(),
                notificationPage.get().map(noti -> notificationMapper.mapToDto(noti, fromUserMap)).toList(),
                notificationPage.getTotalElements()
        ));
        response.setMessage(MessageCode.Notification.NOTIFICATION_FETCHED);

        return response;
    }

    @Override
    public ResponseVM<?> detachNotification(String tokenHeader, String notificationId) {
        CommonNotification notification = repository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Notification.NOTIFICATION_NOT_FOUND));

        if (notification.getToUsers().stream().noneMatch(
                user -> SecurityContextUtils.getUserId().equals(user.getUserId())
        ))
            throw new BadRequestException(MessageCode.Notification.NOTIFICATION_NOT_PERMITTED);

        repository.deleteById(notificationId);

        return new ResponseVM<>(MessageCode.Notification.NOTIFICATION_DETACHED, null, 200);
    }

    @Override
    public ResponseVM<?> testGetResource() {
        return new ResponseVM<>().withMessage(Constants.Firebase.NOTIFICATION_PUBLISH_URL);
    }

    @Override
    public ResponseVM<?> readNotification(String tokenHeader, String notificationId) {
        CommonNotification notification = repository.findById(notificationId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Notification.NOTIFICATION_NOT_FOUND));

        if (notification.getToUsers().stream().noneMatch(
                user -> SecurityContextUtils.getUserId().equals(user.getUserId())
        ))
            throw new BadRequestException(MessageCode.Notification.NOTIFICATION_NOT_PERMITTED);

        notification.setToUsers(
                notification.getToUsers().stream().peek(
                        user -> {
                            if (SecurityContextUtils.getUserId().equals(user.getUserId()))
                                user.setHasRead(true);
                        }
                ).toList()
        );

        repository.save(notification);

        return new ResponseVM<>(MessageCode.Notification.NOTIFICATION_READ, null, 200);
    }
}