package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.RoomResponse;
import vn.edu.tdtu.mapper.RoomResponseMapper;
import vn.edu.tdtu.model.Room;
import vn.edu.tdtu.repository.RoomRepository;
import vn.edu.tdtu.service.interfaces.RoomService;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomResponseMapper roomResponseMapper;

    @Override
    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public Room findById(String id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public Room findExistingRoom(String fromUserId, String toUserId) {
        return roomRepository.findByUserId1AndUserId2OrUserId2AndUserId1(fromUserId, toUserId, fromUserId, toUserId)
                .orElse(null);
    }

    @Override
    public ResponseVM<?> findRoomsByUser() {
        String userId = SecurityContextUtils.getUserId();
        List<Room> rooms = roomRepository.findByUserId1OrUserId2(userId, userId);

        ResponseVM<List<RoomResponse>> response = new ResponseVM<>();
        response.setCode(200);
        response.setMessage(MessageCode.Message.ROOM_FETCHED);
        response.setData(rooms.stream()
                .map(room -> roomResponseMapper.mapToDTO(userId, room))
                .sorted((room1, room2) -> {
                    Date room1CompareDate = room1.getLatestMessage() != null ? room1.getLatestMessage().getCreatedAt() : room1.getCreatedAt(),
                            room2CompareDate = room2.getLatestMessage() != null ? room2.getLatestMessage().getCreatedAt() : room2.getCreatedAt();

                    return room2CompareDate.compareTo(room1CompareDate);
                })
                .toList());

        return response;
    }

    @Override
    public ResponseVM<?> archiveRoom(String opponentId, boolean archived) {
        String authUserId = SecurityContextUtils.getUserId();
        Room foundRoom = findExistingRoom(authUserId, opponentId);

        if (foundRoom == null)
            throw new BadRequestException(MessageCode.Message.ROOM_NOT_FOUND);

        foundRoom.setArchived(archived);

        roomRepository.save(foundRoom);

        return new ResponseVM<>(
                String.format("Room %s successfully", archived ? "archived" : "unarchived"),
                null,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> deleteRoom(String opponentId) {
        return null;
    }
}
