package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Banning;

@Repository
public interface BanningRepository extends JpaRepository<Banning, String> {
}
