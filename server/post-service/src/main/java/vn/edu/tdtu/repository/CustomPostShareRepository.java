package vn.edu.tdtu.repository;

import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.PostShare;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomPostShareRepository {
    public List<PostShare> findSharedPostIdsByFriends(List<String> friendIds, String userId, LocalDateTime startTime);
}
