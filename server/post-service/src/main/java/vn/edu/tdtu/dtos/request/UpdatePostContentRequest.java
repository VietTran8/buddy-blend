package vn.edu.tdtu.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPrivacy;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostContentRequest {
    private String id;
    private String content;
    private EPrivacy privacy;
    private List<String> taggingUsers;
    private List<String> videoUrls;
    private List<String> imageUrls;
}