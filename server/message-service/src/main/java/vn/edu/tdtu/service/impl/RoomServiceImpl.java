package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.RoomResponse;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.RoomResponseMapper;
import vn.edu.tdtu.model.Room;
import vn.edu.tdtu.repository.RoomRepository;
import vn.edu.tdtu.service.interfaces.RoomService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomResponseMapper roomResponseMapper;

    @Override
    public void saveRoom(Room room){
        roomRepository.save(room);
    }

    @Override
    public Room findById(String id){
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public Room findExistingRoom(String fromUserId, String toUserId){
        return roomRepository.findByUserId1AndUserId2OrUserId2AndUserId1(fromUserId, toUserId, fromUserId, toUserId)
                .orElse(null);
    }

    @Override
    public ResDTO<?> findRoomsByUser(){
        String userId = SecurityContextUtils.getUserId();
        List<Room> rooms = roomRepository.findByUserId1OrUserId2(userId, userId);

        ResDTO<List<RoomResponse>> response = new ResDTO<>();
        response.setCode(200);
        response.setMessage("success");
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
    public ResDTO<?> archiveRoom(String opponentId, boolean archived) {
        String authUserId = SecurityContextUtils.getUserId();
        Room foundRoom = findExistingRoom(authUserId, opponentId);

        if(foundRoom == null)
            throw new BadRequestException("Room not found!");

        foundRoom.setArchived(archived);

        roomRepository.save(foundRoom);

        return new ResDTO<>(
                String.format("Room %s successfully", archived ? "archived" : "unarchived"),
                null,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResDTO<?> deleteRoom(String opponentId) {
        return null;
    }
}
