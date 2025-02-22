package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.model.Media;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePostRequest {
    private String content;
    private List<Media> medias;
    private EPrivacy privacy;
    private Boolean active;
    private EPostType type;
    private String groupId;
    private String background;
    private List<PostTagReqDTO> postTags;
}