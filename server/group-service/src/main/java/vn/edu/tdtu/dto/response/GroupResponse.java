package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EJoinGroupStatus;
import vn.edu.tdtu.model.Group;

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
