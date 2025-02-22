package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Post;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomPostRepository {
    Page<Post> findNewsFeed(String userId, List<String> friendIds, List<String> groupId, LocalDateTime startTime, int page, int size);
}
