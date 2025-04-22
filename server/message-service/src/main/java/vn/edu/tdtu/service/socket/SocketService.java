package vn.edu.tdtu.service.socket;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.MessageNoti;
import vn.edu.tdtu.mapper.RoomResponseMapper;
import vn.edu.tdtu.message.JoinRoomMessage;
import vn.edu.tdtu.message.SeenMessage;
import vn.edu.tdtu.message.SendMessage;
import vn.edu.tdtu.model.ChatMessage;
import vn.edu.tdtu.model.Room;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.service.interfaces.ChatMessageService;
import vn.edu.tdtu.service.interfaces.RoomService;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocketService {
    private final RoomService roomService;
    private final KafkaEventPublisher kafkaMsgService;
    private final ChatMessageService chatMessageService;

    public void sendSocketMessage(SocketIOClient senderClient, Room room, ChatMessage message) {
        senderClient.getNamespace()
                .getRoomOperations(room.getId())
                .getClients()
                .forEach(client -> {
                    client.sendEvent("receive_message", RoomResponseMapper.mapMsgToMsgResponse(client.get("userId"), message));
                });
    }

    private void sendRoomJoinedMessage(SocketIOClient senderClient, String fromUserId, Room room) {
        senderClient.getNamespace()
                .getRoomOperations(room.getId())
                .getClients()
                .forEach(client -> {
                    if (client.getSessionId().equals(senderClient.getSessionId())) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("roomId", room.getId());

                        client.sendEvent("joined", data);
                    }
                });
    }

    public void saveMessage(SocketIOClient senderClient, SendMessage messageDto) {
        Room foundRoom = roomService.findExistingRoom(senderClient.get("userId"), messageDto.getToUserId());

        if (foundRoom != null) {
            ChatMessage newMessage = ChatMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .content(messageDto.getContent())
                    .createdAt(new Date())
                    .fromUserId(senderClient.get("userId"))
                    .toUserId(messageDto.getToUserId())
                    .roomId(foundRoom.getId())
                    .medias(messageDto.getMedias())
                    .build();

            ChatMessage savedMessage = chatMessageService.saveMessage(newMessage);

            foundRoom.setLatestMessage(savedMessage);

            roomService.saveRoom(foundRoom);

            kafkaMsgService.publishMessageNoti(new MessageNoti(savedMessage));

            sendSocketMessage(senderClient, foundRoom, savedMessage);
        }
    }

    public void joinRoom(SocketIOClient senderClient, JoinRoomMessage message) {
        senderClient.getAllRooms().forEach(senderClient::leaveRoom);

        Room room = roomService.findExistingRoom(senderClient.get("userId"), message.getToUserId());

        if (Objects.isNull(room)) {
            room = Room.builder()
                    .userId1(senderClient.get("userId"))
                    .userId2(message.getToUserId())
                    .latestMessage(null)
                    .createdAt(new Date())
                    .build();
        }

        if (room.getUserId1().equals(senderClient.get("userId"))) {
            room.setUser1LastSeenTime(new Date());
        } else if (room.getUserId2().equals(senderClient.get("userId"))) {
            room.setUser2LastSeenTime(new Date());
        }

        roomService.saveRoom(room);

        log.info("User ID[{}] with session id {} Connected to room [{}]", senderClient.get("userId"), senderClient.getSessionId().toString(), room.getId());

        senderClient.joinRoom(room.getId());
        sendRoomJoinedMessage(senderClient, senderClient.get("userId"), room);

        senderClient.getNamespace()
                .getRoomOperations(room.getId())
                .getClients()
                .forEach(client -> {
                    if (message.getToUserId().equals(client.get("userId"))) {
                        client.sendEvent("recipient_seen", true);
                    }
                });
    }

    public void updateSeenTime(SocketIOClient senderClient, String userId, SeenMessage message) {
        Room room = roomService.findExistingRoom(userId, message.getFromUserId());

        if (room != null) {
            if (room.getUserId1().equals(userId)) {
                room.setUser1LastSeenTime(new Date());
            } else if (room.getUserId2().equals(userId)) {
                room.setUser2LastSeenTime(new Date());
            }

            roomService.saveRoom(room);

            senderClient.getNamespace()
                    .getRoomOperations(room.getId())
                    .getClients()
                    .forEach(client -> {
                        if (!userId.equals(client.get("userId"))) {
                            client.sendEvent("recipient_seen", true);
                        }
                    });
        }
    }

    public void exitAllRoom(SocketIOClient senderClient) {
        senderClient.getAllRooms().forEach(senderClient::leaveRoom);
    }
}
