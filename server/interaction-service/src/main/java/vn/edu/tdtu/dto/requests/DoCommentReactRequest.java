package vn.edu.tdtu.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.enums.interaction.EReactionType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoCommentReactRequest {
    private String cmtId;
    private EReactionType type;
}
