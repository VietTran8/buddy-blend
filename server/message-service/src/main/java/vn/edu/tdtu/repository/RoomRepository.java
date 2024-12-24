package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByUserId1AndUserId2OrUserId2AndUserId1(String userId1, String userId2, String userId22, String userId12);
    List<Room> findByUserId1OrUserId2(String userId1, String userId2);
}
