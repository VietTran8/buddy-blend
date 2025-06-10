package vn.tdtu.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.tdtu.common.enums.post.EFileType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaDTO implements Serializable {
    @Schema(description = "Unique identifier for the media")
    private String id;

    @Schema(description = "URL of the media file")
    private String url;

    @Schema(description = "Type of the media file (e.g., image, video)")
    private EFileType type;

    @Schema(description = "Identifier of the owner of the media")
    private String ownerId;

    @Schema(description = "URL of the thumbnail for the media")
    private String thumbnail;
}
