package vn.edu.tdtu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
}
