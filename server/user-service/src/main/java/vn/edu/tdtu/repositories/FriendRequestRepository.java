package vn.edu.tdtu.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.tdtu.enums.EFriendReqStatus;
import vn.edu.tdtu.models.FriendRequest;
import vn.edu.tdtu.models.User;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    List<FriendRequest> findByToUserAndActiveAndStatus(User toUser, boolean active, EFriendReqStatus status);
    List<FriendRequest> findByFromUserAndStatusOrToUserAndStatus(User fromUser, EFriendReqStatus status1, User toUser, EFriendReqStatus status2);
    List<FriendRequest> findByToUserAndFromUserOrFromUserAndToUser(User toUser, User fromUser, User fromUser2, User toUser2);
}
