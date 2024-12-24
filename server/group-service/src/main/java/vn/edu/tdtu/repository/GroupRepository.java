package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.dto.response.GroupWithPendingResponse;
import vn.edu.tdtu.model.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
    @Query("SELECT new vn.edu.tdtu.dto.response.GroupWithPendingResponse(g, gm.isPending) FROM UserGroup g JOIN g.groupMembers gm JOIN gm.member m WHERE g.isDeleted = false AND m.userId = :userId")
    List<GroupWithPendingResponse> findJoinedGroups(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM UserGroup g JOIN g.groupMembers gm JOIN gm.member m WHERE g.isDeleted = false AND g.id = :groupId AND m.userId = :userId")
    boolean getIsJoinedToGroup(@Param("groupId") String groupId, @Param("userId") String userId);

    Optional<Group> findByIdAndIsDeleted(String id, boolean isDeleted);
}
