package vn.edu.tdtu.dto.response;

import lombok.*;
import vn.edu.tdtu.enums.EJoinGroupStatus;
import vn.edu.tdtu.model.Group;
import vn.edu.tdtu.model.data.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupResponse extends Group {
    private List<GroupMemberResponse> firstTenMembers;
    private boolean isJoined;
    private boolean isAdmin;
    private Long pendingMemberCount;
    private long memberCount;
    private String currentMemberId;
    private EJoinGroupStatus joinStatus;
}
