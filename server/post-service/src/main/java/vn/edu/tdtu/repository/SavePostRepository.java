package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.SavePost;

import java.util.Optional;

@Repository
public interface SavePostRepository extends MongoRepository<SavePost, String> {
    boolean existsByUserIdAndPostIdsContains(String userId, String postId);
    Optional<SavePost> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
