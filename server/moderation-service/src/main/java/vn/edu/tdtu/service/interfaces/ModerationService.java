package vn.edu.tdtu.service.interfaces;


import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.edu.tdtu.message.ModerateMessage;

import java.util.List;

public interface ModerationService {
    ModerateResponseDto moderateImage(String imageUrl);

    ModerateResponseDto bulkModerateImages(List<String> imageUrls);

    ModerateResponseDto moderateText(String message);

    ModerateResponseDto moderate(ModerateMessage message);
}
