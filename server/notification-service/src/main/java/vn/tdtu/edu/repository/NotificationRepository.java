package vn.tdtu.edu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.tdtu.edu.model.InteractNotification;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<InteractNotification, String> {
    List<InteractNotification> findAllByToUserId(String toUserId);
    void deleteByIdAndToUserId(String id, String toUserId);
}
