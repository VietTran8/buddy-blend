package vn.tdtu.edu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tdtu.edu.model.CommonNotification;

@Repository
public interface NotificationRepository extends MongoRepository<CommonNotification, String> {
    @Query(value = "{ 'toUserIds': ?0 }", sort = "{ 'createAt': -1 }")
    Page<CommonNotification> findByToUserId(String toUserId, Pageable pageable);
    void deleteByIdAndToUserIdsContaining(String id, String toUserId);
}
