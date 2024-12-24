package vn.edu.tdtu.mapper.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.FriendReqDTO;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.repository.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AddFriendReqMapper {
    private final UserRepository userRepository;
    public FriendRequest mapToObject(String fromUserId, FriendReqDTO dto){
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setActive(true);
        friendRequest.setCreatedAt(LocalDateTime.now());
        friendRequest.setUpdatedAt(LocalDateTime.now());
        friendRequest.setStatus(EFriendReqStatus.PENDING);
        friendRequest.setFromUser(userRepository.findByIdAndActive(fromUserId, true).orElse(null));
        friendRequest.setToUser(userRepository.findByIdAndActive(dto.getToUserId(), true).orElse(null));

        return friendRequest;
    }
}
