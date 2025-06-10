package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.MessageResponse;
import vn.edu.tdtu.dto.RoomResponse;
import vn.edu.tdtu.model.ChatMessage;
import vn.edu.tdtu.model.Room;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.UserDTO;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomResponseMapper {
    private final UserService userService;

    public RoomResponse mapToDTO(String currentUserId, Room object) {
        RoomResponse response = new RoomResponse();
        response.setId(object.getId());
        response.setCreatedAt(object.getCreatedAt());
        response.setLatestMessage(object.getLatestMessage());
        response.setLastSentByYou(
                response.getLatestMessage() != null
                        && response.getLatestMessage().getFromUserId().equals(currentUserId)
        );
        UserDTO opponentUser = new UserDTO();
        String opponentUserId = "";

        if (object.getUserId1().equals(currentUserId)) {
            opponentUserId = object.getUserId2();
            response.setOpponentLastSeenTime(object.getUser2LastSeenTime());
        } else {
            opponentUserId = object.getUserId1();
            response.setOpponentLastSeenTime(object.getUser1LastSeenTime());
        }

        opponentUser = userService.findById(opponentUserId);

        if (opponentUser != null) {
            response.setRoomImage(opponentUser.getProfilePicture());
            response.setRoomName(opponentUser.getUserFullName());
            response.setOpponentUserId(opponentUserId);
            response.setOnline(opponentUser.isOnline());
        }

        return response;
    }

    public static MessageResponse mapMsgToMsgResponse(String currentUserId, ChatMessage msg) {
        MessageResponse msgResponse = new MessageResponse();

        msgResponse.setSentByYou(currentUserId.equals(msg.getFromUserId()));
        msgResponse.setId(msg.getId());
        msgResponse.setContent(msg.getContent());
        msgResponse.setCreatedAt(msg.getCreatedAt());
        msgResponse.setMedias(msg.getMedias());
        msgResponse.setFromUserId(msg.getFromUserId());
        msgResponse.setToUserId(msg.getToUserId());

        return msgResponse;
    }
}
