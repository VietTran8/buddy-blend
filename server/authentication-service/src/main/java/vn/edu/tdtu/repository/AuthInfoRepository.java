package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.AuthInfo;

import java.util.Optional;

@Repository
public interface AuthInfoRepository extends JpaRepository<AuthInfo, String> {
    Optional<AuthInfo> findByEmail(String email);
    Boolean existsByEmail(String email);
}
