package vn.edu.tdtu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.GroupMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {
    List<GroupMember> findByGroupAndIsPending(Group group, boolean pending);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.id = :memberId " +
            "AND m.isPending = false " +
            "AND m.group.id = :groupId " +
            "AND m.group.isDeleted = false ")
    Optional<GroupMember> findByGroupIdAndMemberId(@Param("groupId") String groupId, @Param("memberId") String memberId);
    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.id = :memberId")
    void deleteMemberById(@Param("memberId") String memberId);
    @Query("SELECT gm FROM GroupMember gm JOIN gm.group ug WHERE " +
            "ug.id = :groupId " +
            "AND ug.isDeleted = false " +
            "AND gm.id = :memberId " +
            "AND gm.isPending = true")
    Optional<GroupMember> findPendingMemberInGroup(@Param("groupId") String groupId, @Param("memberId") String memberId);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.group.id = :groupId " +
            "AND m.group.isDeleted = false " +
            "AND m.isPending = false " +
            "AND m.joinedAt >= :threeDaysAgo " +
            "ORDER BY m.joinedAt DESC")
    Page<GroupMember> findNewMembersByGroupId(@Param("groupId") String groupId, @Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.group.id = :groupId " +
            "AND m.group.isDeleted = false " +
            "AND m.isAdmin = true")
    Page<GroupMember> findAdminMembersByGroupId(@Param("groupId") String groupId, Pageable pageable);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.group.id = :groupId " +
            "AND m.group.isDeleted = false " +
            "AND m.isPending = false " +
            "ORDER BY m.joinedAt DESC")
    Page<GroupMember> findAllMembersByGroupId(@Param("groupId") String groupId, Pageable pageable);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.group.id = :groupId " +
            "AND m.group.isDeleted = false " +
            "AND m.isPending = false " +
            "ORDER BY m.joinedAt DESC")
    List<GroupMember> findAllMembersByGroupId(@Param("groupId") String groupId);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.group.id = :groupId " +
            "AND m.group.isDeleted = false " +
            "AND m.isPending = false " +
            "AND m.member.userId IN :friendIds " +
            "ORDER BY m.joinedAt DESC")
    Page<GroupMember> findFriendMembersByGroupId(@Param("groupId") String groupId, @Param("friendIds") List<String> friendIds, Pageable pageable);
    @Query("SELECT m FROM GroupMember m WHERE " +
            "m.group.id = :groupId " +
            "AND m.group.isDeleted = false " +
            "AND m.isPending = false " +
            "AND m.member.userId IN :friendIds " +
            "ORDER BY m.joinedAt DESC")
    List<GroupMember> findFriendMembersByGroupId(@Param("groupId") String groupId, @Param("friendIds") List<String> friendIds);
}
