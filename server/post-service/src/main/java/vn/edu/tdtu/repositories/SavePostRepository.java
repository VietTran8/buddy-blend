package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.SavePost;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavePostRepository extends MongoRepository<SavePost, String> {
    Optional<SavePost> findByUserId(String userId);
    boolean existsByUserId(String userId);
}