package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.Media;
import vn.tdtu.common.enums.post.EPrivacy;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostContentRequest {
    private String id;
    private String content;
    private EPrivacy privacy;
    private String background;
    private List<String> taggingUsers;
    private List<Media> medias;
}