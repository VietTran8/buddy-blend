package vn.edu.tdtu.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EFriendReqStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HandleFriendRequestResponse {
    private String requestId;
    private EFriendReqStatus status;
}
