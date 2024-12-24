package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.PostShare;

import java.util.List;

@Repository
public interface PostShareRepository extends MongoRepository<PostShare, String> {
    List<PostShare> findBySharedPostId(String sharedPost);
    List<PostShare> findBySharedUserId(String sharedUserId);
    List<PostShare> findByIdIn(List<String> id);
    @Query("{ 'normalizedStatus' : { $regex: ?0, $options: 'i' } }")
    List<PostShare> findByContent(String key);
}
