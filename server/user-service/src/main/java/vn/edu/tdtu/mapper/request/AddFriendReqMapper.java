package vn.edu.tdtu.mapper.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.request.FriendReqDTO;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.models.FriendRequest;
import vn.edu.tdtu.services.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AddFriendReqMapper {
    private final UserService userService;
    public FriendRequest mapToObject(String fromUserId, FriendReqDTO dto){
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setActive(true);
        friendRequest.setCreatedAt(LocalDateTime.now());
        friendRequest.setUpdatedAt(LocalDateTime.now());
        friendRequest.setStatus(EFriendReqStatus.PENDING);
        friendRequest.setFromUser(userService.findById(fromUserId));
        friendRequest.setToUser(userService.findById(dto.getToUserId()));

        return friendRequest;
    }
}
