package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.models.Reactions;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<Reactions, String> {
    Optional<Reactions> findByUserIdAndPostId(String userId, String postId);
    List<Reactions> findReactionsByPostIdOrderByCreatedAtDesc(String postId);
}
