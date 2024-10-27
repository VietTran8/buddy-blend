package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.GroupMember;
import vn.edu.tdtu.model.data.User;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GroupMemberResponse extends GroupMember {
    User user;

    public GroupMemberResponse(GroupMember groupMember, User user) {
        super(
                groupMember.getId(),
                groupMember.isAdmin(),
                groupMember.isPending(),
                groupMember.getJoinedAt(),
                groupMember.getGroup(),
                groupMember.getMember()
        );
        this.user = user;
    }
}
