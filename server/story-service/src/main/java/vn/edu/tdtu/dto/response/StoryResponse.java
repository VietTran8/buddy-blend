package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.data.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryResponse extends Story {
    private User user;
    private boolean isMine;
    private int storyCount;
    private boolean seen;
    private Long viewCount;
}
