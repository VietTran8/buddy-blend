package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Report;

import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    Optional<Report> findByIdAndActiveOrActive(String id, Boolean active, Boolean active2);

    Page<Report> findAllByActiveOrActive(Boolean active, Boolean active2, Pageable pageable);
}
