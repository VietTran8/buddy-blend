package vn.edu.tdtu.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EReactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoReactRequest {
    private String postId;
    private EReactionType type;
}
