package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EModerateType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerateResultsMessage {
    private String refId;
    private boolean accept;
    private String rejectReason;
    private String timestamp;
    private EModerateType type;
}
