package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EReactionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoReactRequest {
    private String storyId;
    private EReactionType type;
}
