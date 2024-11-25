package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.Group;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupWithPendingResponse extends Group {
    private boolean isPending;
    private long memberCount;

    public GroupWithPendingResponse(Group group, boolean isPending) {
        super(
                group.getId(),
                group.getName(),
                group.getPrivacy(),
                group.getDescription(),
                group.getAvatar(),
                group.getCover(),
                group.isDeleted(),
                group.getCreatedAt(),
                group.getCreatedBy(),
                group.getGroupMembers(),
                group.getContributions()
        );
        this.isPending = isPending;
        this.memberCount = group.getGroupMembers().stream().filter(member -> !member.isPending()).count();
    }
}
