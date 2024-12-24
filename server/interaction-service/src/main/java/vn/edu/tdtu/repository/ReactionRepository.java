package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Reactions;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<Reactions, String> {
    Optional<Reactions> findByUserIdAndPostId(String userId, String postId);
    List<Reactions> findReactionsByPostIdOrderByCreatedAtDesc(String postId);
}
