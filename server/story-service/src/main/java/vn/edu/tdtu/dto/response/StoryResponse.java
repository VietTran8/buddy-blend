package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.Story;
import vn.tdtu.common.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryResponse extends Story {
    private UserDTO user;
    private boolean isMine;
    private int storyCount;
    private boolean seen;
    private Long viewCount;
}
