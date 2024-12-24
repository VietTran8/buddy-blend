package vn.edu.tdtu.service.interfaces;


import vn.edu.tdtu.dto.ModerateResponseDto;

import java.util.List;

public interface ModerationService {
    public ModerateResponseDto moderateImage(String imageUrl);
    public ModerateResponseDto bulkModerateImages(List<String> imageUrls);
}
