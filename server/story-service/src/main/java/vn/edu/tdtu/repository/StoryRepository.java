package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.dto.query.UserStory;
import vn.edu.tdtu.model.Story;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    int countByExpiredAtAfterAndUserId(LocalDateTime expiredAt, String userId);

    @Query("SELECT s FROM Story s WHERE s.expiredAt > :expired AND (s.userId = :userId OR s.userId IN :friendIds)")
    List<Story> findStories(
            @Param("expired") LocalDateTime expired,
            @Param("userId") String userId,
            @Param("friendIds") List<String> friendIds
    );
}
