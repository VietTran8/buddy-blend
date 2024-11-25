package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Member;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByUserId(String userId);

}