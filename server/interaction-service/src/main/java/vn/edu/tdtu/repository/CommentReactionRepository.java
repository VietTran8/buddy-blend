package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.CommentReactions;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends MongoRepository<CommentReactions, String> {
    Optional<CommentReactions> findByUserIdAndCmtId(String userId, String cmtId);

    List<CommentReactions> findByCmtId(String cmtId);

    List<CommentReactions> findReactionsByCmtIdOrderByCreatedAtDesc(String cmtId);
}
