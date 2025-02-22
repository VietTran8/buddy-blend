package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Media;

import java.util.Collection;
import java.util.List;

@Repository
public interface MediaRepository extends MongoRepository<Media, String> {
    void deleteByIdIn(Collection<String> id);
    Page<Media> findByOwnerIdAndDetachedNot(String ownerId, boolean detached, Pageable pageable);
}
