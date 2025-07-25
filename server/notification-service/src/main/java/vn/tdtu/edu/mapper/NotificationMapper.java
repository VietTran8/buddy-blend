package vn.tdtu.edu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.edu.dto.NotificationResponse;
import vn.tdtu.edu.model.CommonNotification;
import vn.tdtu.edu.model.UserInfo;
import vn.tdtu.edu.service.interfaces.UserService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final UserService userService;

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

    public NotificationResponse mapToDto(CommonNotification commonNotification, Map<String, UserDTO> fromUserMap) {
        UserDTO foundFromUser = fromUserMap.get(commonNotification.getFromUserId());

        NotificationResponse response = baseMap(commonNotification);

        response.setFromUser(foundFromUser);

        return response;
    }
}
