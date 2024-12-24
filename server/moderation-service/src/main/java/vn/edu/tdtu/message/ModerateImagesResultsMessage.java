package vn.edu.tdtu.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.tdtu.dto.ModerateResponseDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerateImagesResultsMessage extends ModerateResponseDto {
    private String postId;
    private String timestamp;

    public ModerateImagesResultsMessage(ModerateResponseDto dto, String postId){
        this.postId = postId;
        this.setAccept(dto.isAccept());
        this.setRejectReason(dto.getRejectReason());
        this.setTimestamp(String.valueOf(System.currentTimeMillis()));
    }
}
