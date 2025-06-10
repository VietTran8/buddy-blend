package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{ 'normalizedContent' : { $regex: ?0, $options: 'i' }, 'detached': { $ne: true } }")
    List<Post> findByContent(String key);

    @Query("""
              {
                   '$and': [
                     { 'detached': { $ne: true } },
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
    Page<Post> findByUserIdOrPostTagsTaggedUserIdWithPrivacy(
            String userId,
            String taggedUserId,
            List<String> myFriendIds,
            String authUserId,
            Pageable pageable
    );

    Page<Post> findByGroupIdAndDetachedNotOrderByCreatedAtDesc(String groupId, boolean detached, Pageable pageable);

    List<Post> findByIdInAndDetachedNot(Collection<String> id, boolean detached);

    Optional<Post> findByIdAndDetached(String id, boolean detached);

    Optional<Post> findByIdAndDetachedAndUserId(String id, boolean detached, String userId);
}
