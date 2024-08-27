package vn.edu.tdtu.repositories;

import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.PostShare;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomPostShareRepository {
    public List<PostShare> findSharedPostIdsByFriends(List<String> friendIds, String userId, LocalDateTime startTime);
}
