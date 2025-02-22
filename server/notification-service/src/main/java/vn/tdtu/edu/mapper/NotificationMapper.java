package vn.tdtu.edu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.tdtu.edu.dto.NotificationResponse;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.model.UserInfo;
import vn.tdtu.edu.model.data.User;
import vn.tdtu.edu.service.interfaces.UserService;
import vn.tdtu.edu.util.SecurityContextUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final UserService userService;
    public NotificationResponse mapToDto(CommonNotification commonNotification) {
        User foundFromUser = userService.findById(commonNotification.getFromUserId());

        NotificationResponse response = baseMap(commonNotification);

        response.setFromUser(foundFromUser);

        return response;
    }

    private NotificationResponse baseMap(CommonNotification commonNotification) {
        String authUserId = SecurityContextUtils.getUserId();
        UserInfo authUserInfo = commonNotification.getToUsers()
                .stream()
                .filter(user -> authUserId.equals(user.getUserId()))
                .findFirst()
                .orElse(null);

        NotificationResponse response = new NotificationResponse();
        response.setContent(commonNotification.getContent());
        response.setType(commonNotification.getType());
        response.setId(commonNotification.getId());
        response.setCreateAt(commonNotification.getCreateAt());
        response.setTitle(commonNotification.getTitle());
        response.setRefId(commonNotification.getRefId());
        response.setToUserId(authUserId);
        response.setHasRead(authUserInfo != null && authUserInfo.isHasRead());

        return response;
    }

    public NotificationResponse mapToDto(CommonNotification commonNotification, Map<String, User> fromUserMap) {
        User foundFromUser = fromUserMap.get(commonNotification.getFromUserId());

        NotificationResponse response = baseMap(commonNotification);

        response.setFromUser(foundFromUser);

        return response;
    }
}
