package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.Viewer;

import java.util.Optional;

@Repository
public interface ViewerRepository extends JpaRepository<Viewer, String> {
    Optional<Viewer> findByStoryAndUserId(Story story, String userId);
}
