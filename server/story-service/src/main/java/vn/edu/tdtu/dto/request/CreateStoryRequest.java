package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.enums.EMediaType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.enums.EStoryFont;
import vn.edu.tdtu.enums.EStoryType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateStoryRequest {
    private String mediaUrl;
    private String thumbnailUrl;
    private EMediaType mediaType;
    private EPrivacy privacy;
    private EStoryType storyType;
    private EStoryFont font;
    private String content;
    private String background;
}
