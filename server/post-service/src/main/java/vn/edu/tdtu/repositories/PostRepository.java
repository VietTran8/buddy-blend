package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.Post;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{ 'normalizedContent' : { $regex: ?0, $options: 'i' } }")
    List<Post> findByContent(String key);
    List<Post> findByUserIdOrPostTagsTaggedUserId(String userId, String postTags_taggedUser_id);
}
