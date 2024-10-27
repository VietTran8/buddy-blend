package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EMemberAcceptation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerateMemberRequest {
    String memberId;
    String groupId;
    EMemberAcceptation acceptOption;
    String reason;
}
