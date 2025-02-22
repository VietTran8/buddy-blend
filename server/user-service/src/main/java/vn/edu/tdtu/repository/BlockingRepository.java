package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Blocking;

@Repository
public interface BlockingRepository extends JpaRepository<Blocking, String> {
}
