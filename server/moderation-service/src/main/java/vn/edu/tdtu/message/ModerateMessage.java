package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.moderation.EModerateType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModerateMessage {
    private List<String> imageUrls;
    private String refId;
    private String content;
    private EModerateType type;
}
