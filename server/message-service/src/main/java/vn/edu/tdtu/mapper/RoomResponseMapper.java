package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.MessageResponse;
import vn.edu.tdtu.dto.RoomResponse;
import vn.edu.tdtu.model.Message;
import vn.edu.tdtu.model.Room;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.service.UserService;

@Component
@RequiredArgsConstructor
public class RoomResponseMapper {
    private final UserService userService;
    public RoomResponse mapToDTO(String currentUserId, Room object){

        RoomResponse response = new RoomResponse();
        response.setId(object.getId());
        response.setMessages(object.getMessages().stream().map(
                msg -> mapMsgToMsgResponse(currentUserId, msg)
        ).toList());
        response.setCreatedAt(object.getCreatedAt());
        response.setUserId2(object.getUserId2());
        response.setUserId1(object.getUserId1());
        response.setLatestMessage(
                object.getMessages()
                        .stream()
                        .max((msg1, msg2) -> msg1.getCreatedAt().compareTo(msg2.getCreatedAt()))
                        .map(msg -> mapMsgToMsgResponse(currentUserId, msg))
                        .orElse(null)
        );
        User opponentUser = new User();
        String opponentUserId = "";
        if(object.getUserId1().equals(currentUserId)){
           opponentUserId = object.getUserId2();
        }else{
            opponentUserId = object.getUserId1();
        }

        opponentUser = userService.findById(opponentUserId);

        if(opponentUser != null) {
            response.setRoomImage(opponentUser.getProfilePicture());
            response.setRoomName(opponentUser.getUserFullName());
            response.setOpponentUserId(opponentUserId);
        }

        return response;
    }

    public static MessageResponse mapMsgToMsgResponse (String currentUserId, Message msg) {
            MessageResponse msgResponse = new MessageResponse();

            msgResponse.setSentByYou(currentUserId.equals(msg.getFromUserId()));
            msgResponse.setRead(msg.isRead());
            msgResponse.setId(msg.getId());
            msgResponse.setContent(msg.getContent());
            msgResponse.setCreatedAt(msg.getCreatedAt());
            msgResponse.setImageUrls(msg.getImageUrls());
            msgResponse.setFromUserId(msg.getFromUserId());
            msgResponse.setToUserId(msg.getToUserId());

            return msgResponse;
    }
}