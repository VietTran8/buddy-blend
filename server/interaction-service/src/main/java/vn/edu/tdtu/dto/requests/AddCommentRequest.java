package vn.edu.tdtu.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddCommentRequest {
    private String content;
    private String parentId;
    private List<String> imageUrls;
    private String postId;
}
