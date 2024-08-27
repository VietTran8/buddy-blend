package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.CommentReactions;

import java.util.Optional;

@Repository
public interface CommentReactionRepository extends MongoRepository<CommentReactions, String> {
    Optional<CommentReactions> findByUserIdAndCmtId(String userId, String cmtId);
}
