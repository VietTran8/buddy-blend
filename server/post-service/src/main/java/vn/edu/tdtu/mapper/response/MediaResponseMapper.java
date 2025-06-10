package vn.edu.tdtu.mapper.response;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.Media;
import vn.tdtu.common.dto.MediaDTO;

@Component
public class MediaResponseMapper {

    public MediaDTO mapToDTO(Media media) {
        if (media == null) {
            return null;
        }

        MediaDTO mediaDTO = new MediaDTO();
        mediaDTO.setId(media.getId());
        mediaDTO.setUrl(media.getUrl());
        mediaDTO.setType(media.getType());
        mediaDTO.setOwnerId(media.getOwnerId());
        mediaDTO.setThumbnail(media.getThumbnail());

        return mediaDTO;
    }
}
