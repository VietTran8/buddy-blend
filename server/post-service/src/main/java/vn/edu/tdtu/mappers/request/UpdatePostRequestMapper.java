package vn.edu.tdtu.mappers.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.request.UpdatePostContentRequest;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.services.FileService;
import vn.edu.tdtu.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdatePostRequestMapper {
    private final FileService fileService;
    public void bindToObject(UpdatePostContentRequest dto, Post object){
        object.setContent(dto.getContent());
        object.setNormalizedContent(StringUtils.toSlug(object.getContent()));

        List<String> needDelVidUrls = new ArrayList<>(object.getVideoUrls());
        needDelVidUrls.removeAll(dto.getVideoUrls());

        List<String> needDelImgUrls = new ArrayList<>(object.getImageUrls());
        needDelImgUrls.removeAll(dto.getImageUrls());

        if(!needDelImgUrls.isEmpty())
            needDelImgUrls.forEach(url -> {
                fileService.delete(url, EFileType.TYPE_IMG);
            });
        if(!needDelVidUrls.isEmpty())
            needDelVidUrls.forEach(url -> {
                fileService.delete(url, EFileType.TYPE_VIDEO);
            });

        object.setVideoUrls(dto.getVideoUrls());
        object.setImageUrls(dto.getImageUrls());

        if(!object.getPrivacy().equals(dto.getPrivacy())){
            object.setPrivacy(dto.getPrivacy());
        }

        object.setUpdatedAt(LocalDateTime.now());
    }
}
