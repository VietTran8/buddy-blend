package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.BannedWord;

import java.util.Optional;

@Repository
public interface BannedWordRepository extends MongoRepository<BannedWord, String> {
    boolean existsByWord(String word);

    Optional<BannedWord> findByWord(String word);
}
