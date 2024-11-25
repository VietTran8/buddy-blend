package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EJoinGroupStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JoinGroupResponse {
    private String groupId;
    private EJoinGroupStatus status;
}
