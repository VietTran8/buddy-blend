package vn.edu.tdtu.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {
    private String content;
    private List<String> imageUrls;
}
