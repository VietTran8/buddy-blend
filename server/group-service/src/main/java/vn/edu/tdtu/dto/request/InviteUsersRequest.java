package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InviteUsersRequest {
    private List<String> userIds;
    private String groupId;
}
