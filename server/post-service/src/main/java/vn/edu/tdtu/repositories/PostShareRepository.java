package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.models.PostShare;

import java.util.List;

@Repository
public interface PostShareRepository extends MongoRepository<PostShare, String> {
    List<PostShare> findBySharedPostId(String sharedPost);
    List<PostShare> findBySharedUserId(String sharedUserId);
    @Query("{ 'normalizedStatus' : { $regex: ?0, $options: 'i' } }")
    List<PostShare> findByContent(String key);
}
