package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.tdtu.common.enums.moderation.EModerateType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerateResultsMessage extends ModerateResponseDto {
    private String refId;
    private String timestamp;
    private EModerateType type;

    public ModerateResultsMessage(ModerateResponseDto dto, ModerateMessage message) {
        this.refId = message.getRefId();
        this.setAccept(dto.isAccept());
        this.setType(message.getType());
        this.setRejectReason(dto.getRejectReason());
        this.setTimestamp(String.valueOf(System.currentTimeMillis()));
    }
}
