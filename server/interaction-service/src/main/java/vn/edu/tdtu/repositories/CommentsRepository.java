package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.edu.tdtu.models.Comments;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comments, String> {
    List<Comments> findByPostIdAndParentIdIsNull(String postId);
    List<Comments> findByParentId(String parentId);
}