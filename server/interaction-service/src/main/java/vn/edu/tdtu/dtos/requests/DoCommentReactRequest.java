package vn.edu.tdtu.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EReactionType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoCommentReactRequest {
    private String cmtId;
    private EReactionType type;
}
