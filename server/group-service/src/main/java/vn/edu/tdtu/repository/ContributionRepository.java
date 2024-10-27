package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Contribution;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, String> {
}
