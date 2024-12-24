package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.enums.EPrivacy;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharePostRequest {
    private String postId;
    private String status;
    private EPrivacy privacy;
}
