package vn.edu.tdtu.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.Media;
import vn.tdtu.common.enums.post.EPostType;
import vn.tdtu.common.enums.post.EPrivacy;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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