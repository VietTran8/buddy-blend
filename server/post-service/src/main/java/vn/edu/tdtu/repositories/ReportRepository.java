package vn.edu.tdtu.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.models.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
}
