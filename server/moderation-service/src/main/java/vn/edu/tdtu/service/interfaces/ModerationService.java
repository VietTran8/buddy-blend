package vn.edu.tdtu.service.interfaces;


import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.edu.tdtu.message.ModerateMessage;

import java.util.List;

public interface ModerationService {
    public ModerateResponseDto moderateImage(String imageUrl);
    public ModerateResponseDto bulkModerateImages(List<String> imageUrls);
    public ModerateResponseDto moderateText(String message);
    public ModerateResponseDto moderate(ModerateMessage message);
}
