package vn.tdtu.edu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.edu.enums.EModerateType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerateResultsMessage {
    private String refId;
    private boolean accept;
    private String rejectReason;
    private String timestamp;
    private String toUserId;
    private EModerateType type;
}
