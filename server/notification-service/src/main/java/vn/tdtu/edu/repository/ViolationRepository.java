package vn.tdtu.edu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.tdtu.edu.model.Violation;

import java.util.Optional;

@Repository
public interface ViolationRepository extends MongoRepository<Violation, String> {
    Optional<Violation> findByRefId(String refId);
}
