package vn.edu.tdtu.repositories;

import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.Post;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomPostRepository {
    List<Post> findNewsFeed(String userId, List<String> friendIds, LocalDateTime startTime);
}
