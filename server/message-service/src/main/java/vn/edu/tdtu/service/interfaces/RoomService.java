package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.model.Room;

public interface RoomService {
    public void saveRoom(Room room);
    public Room findById(String id);
    public Room findExistingRoom(String fromUserId, String toUserId);
    public ResDTO<?> findRoomsByUser();
    public ResDTO<?> archiveRoom(String opponentId, boolean archive);
    public ResDTO<?> deleteRoom(String opponentId);
}
