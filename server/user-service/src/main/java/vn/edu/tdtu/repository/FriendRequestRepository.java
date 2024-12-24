package vn.edu.tdtu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.model.FriendRequest;
import vn.edu.tdtu.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    List<FriendRequest> findByToUserAndActiveAndStatus(User toUser, boolean active, EFriendReqStatus status);
    List<FriendRequest> findByFromUserAndStatusOrToUserAndStatus(User fromUser, EFriendReqStatus status1, User toUser, EFriendReqStatus status2);
    List<FriendRequest> findByToUserAndFromUserOrFromUserAndToUser(User toUser, User fromUser, User fromUser2, User toUser2);
    @Query("SELECT f FROM FriendRequest f WHERE f.fromUser.id = ?1 AND f.toUser.id = ?2")
    Optional<FriendRequest> findByFromUserIdAndToUserId(String fromUserId, String toUserId);
}
