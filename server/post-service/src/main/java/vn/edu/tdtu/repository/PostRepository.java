package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{ 'normalizedContent' : { $regex: ?0, $options: 'i' } }")
    List<Post> findByContent(String key);
    @Query("""
      {
           '$and': [
             {  'type': { '$ne': 'GROUP' } },
             {
               '$or': [
                 { 'userId': ?0 },
                 { 'postTags.taggedUserId': ?1 }
               ]
             },
             {
               '$or': [
                 {
                   'privacy': 'ONLY_FRIENDS',
                   '$or': [
                     { 'userId': { '$in': ?2 } },
                     { 'userId': ?3 }
                   ]
                 },
                 { 'privacy': 'PRIVATE', 'userId': ?3 },
                 { 'privacy': 'PUBLIC' },
               ]
             }
           ]
     }
    """)
    List<Post> findByUserIdOrPostTagsTaggedUserIdWithPrivacy(
            String userId,
            String taggedUserId,
            List<String> myFriendIds,
            String authUserId
    );
    Page<Post> findByGroupIdOrderByCreatedAtDesc(String groupId, Pageable pageable);
    List<Post> findByIdIn(List<String> id);

}
