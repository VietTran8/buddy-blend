package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.ChatMessage;

import java.util.Date;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    Page<ChatMessage> findByRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(String roomId, Date beforeDate, Pageable pageable);
}
