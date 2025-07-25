package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.Group;
import vn.tdtu.common.enums.group.EGroupPrivacy;

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
                group.getContributions(),
                group.getPrivacy().equals(EGroupPrivacy.PRIVACY_PRIVATE)
        );
        this.isPending = isPending;
        this.memberCount = group.getGroupMembers().stream().filter(member -> !member.isPending()).count();
    }
}
