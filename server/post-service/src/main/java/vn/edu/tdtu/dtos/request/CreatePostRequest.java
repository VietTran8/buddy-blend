package vn.edu.tdtu.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePostRequest {
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private EPrivacy privacy;
    private Boolean active;
    private EPostType type;
    private List<PostTagReqDTO> postTags;
}