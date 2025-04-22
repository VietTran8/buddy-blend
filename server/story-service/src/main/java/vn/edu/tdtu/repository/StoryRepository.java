package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Story;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    int countByExpiredAtAfterAndUserId(LocalDateTime expiredAt, String userId);

    @Query("SELECT s FROM Story s WHERE " +
            "s.expiredAt > :expired " +
            "AND (s.userId = :userId OR s.userId IN :friendIds) " +
            "AND (s.privacy = 'PUBLIC' OR s.privacy = 'ONLY_FRIENDS' OR (s.privacy = 'PRIVATE' AND s.userId = :userId)) " +
            "ORDER BY s.createdAt DESC ")
    List<Story> findStories(
            @Param("expired") LocalDateTime expired,
            @Param("userId") String userId,
            @Param("friendIds") List<String> friendIds
    );

    @Query("SELECT s FROM Story s WHERE " +
            "s.userId = :userId " +
            "AND (" +
            "s.privacy = 'PUBLIC' " +
            "OR (" +
            "s.privacy = 'ONLY_FRIENDS' " +
            "AND (s.userId in :friendIds OR s.userId = :authUserId)" +
            ") " +
            "OR (" +
            "s.privacy = 'PRIVATE' " +
            "AND s.userId = :authUserId" +
            ")" +
            ") " +
            "AND s.expiredAt > :expiredAt " +
            "ORDER BY s.createdAt ASC")
    List<Story> findUserStory(
            @Param("userId") String userId,
            @Param("authUserId") String authUserId,
            @Param("friendIds") List<String> friendIds,
            @Param("expiredAt") LocalDateTime expiredAt);
}
