package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.data.SearchHistory;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, String> {
    List<SearchHistory> findByUserId(String userId);

    void deleteByUserIdAndId(String userId, String id);

    void deleteByUserId(String userId);

    boolean existsByQuery(String query);
}
