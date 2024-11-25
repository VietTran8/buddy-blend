package vn.edu.tdtu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.PaginationResponse;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.RoomResponse;
import vn.edu.tdtu.mapper.RoomResponseMapper;
import vn.edu.tdtu.model.Room;
import vn.edu.tdtu.repository.RoomRepository;
import vn.edu.tdtu.utils.JwtUtils;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RoomRepository roomRepository;
    private final JwtUtils jwtUtils;
    private final RoomResponseMapper roomResponseMapper;

    public void saveRoom(Room room){
        roomRepository.save(room);
    }

    public Room findById(String id){
        return roomRepository.findById(id).orElse(null);
    }

    public Room findExistingRoom(String fromUserId, String toUserId){
        return roomRepository.findByUserId1AndUserId2OrUserId2AndUserId1(fromUserId, toUserId, fromUserId, toUserId)
                .orElse(null);
    }

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
}
