package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.Room;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface RoomService {
    void saveRoom(Room room);

    Room findById(String id);

    Room findExistingRoom(String fromUserId, String toUserId);

    ResponseVM<?> findRoomsByUser();

    ResponseVM<?> archiveRoom(String opponentId, boolean archive);

    ResponseVM<?> deleteRoom(String opponentId);
}
