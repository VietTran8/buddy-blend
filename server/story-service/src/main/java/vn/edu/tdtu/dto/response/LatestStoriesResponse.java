package vn.edu.tdtu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.model.data.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LatestStoriesResponse {
    private User user;
    private StoryResponse latestStory;
    private boolean isSeenAll;
}
