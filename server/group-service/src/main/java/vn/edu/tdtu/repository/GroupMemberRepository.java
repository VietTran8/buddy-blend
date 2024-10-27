package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {
    List<GroupMember> findByGroupAndIsPending(Group group, boolean pending);

    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.id = :memberId")
    void deleteMemberById(@Param("memberId") String memberId);

    @Query("SELECT gm FROM GroupMember gm JOIN gm.group ug WHERE ug.id = :groupId AND gm.id = :memberId AND gm.isPending = true")
    Optional<GroupMember> findPendingMemberInGroup(@Param("groupId") String groupId, @Param("memberId") String memberId);
}
