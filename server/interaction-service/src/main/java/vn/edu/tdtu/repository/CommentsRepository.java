package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.edu.tdtu.model.Comments;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comments, String> {
    List<Comments> findByPostIdAndParentIdIsNull(String postId);

    List<Comments> findByParentId(String parentId);

    long countByPostId(String postId);
}