package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerateImagesResultsMessage {
    private String postId;
    private boolean accept;
    private String rejectReason;
    private String timestamp;
}
