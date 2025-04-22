package vn.edu.tdtu.mapper.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.UpdatePostContentRequest;
import vn.edu.tdtu.enums.EFileType;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.service.intefaces.FileService;
import vn.edu.tdtu.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UpdatePostRequestMapper {
    private final FileService fileService;
    private final MediaRepository mediaRepository;

    public void bindToObject(UpdatePostContentRequest dto, Post object) {
        object.setContent(dto.getContent());
        object.setBackground(dto.getBackground());
        object.setNormalizedContent(StringUtils.toSlug(object.getContent()));

        List<Media> keepMedias = dto.getMedias()
                .stream()
                .filter(media -> media.getId() != null && !media.getId().isEmpty())
                .toList();

//        List<String> needDelVidUrls = new ArrayList<>(object.getVideoUrls() != null ? object.getVideoUrls() : List.of());
//        needDelVidUrls.removeAll(dto.getVideoUrls());
//
//        List<String> needDelImgUrls = new ArrayList<>(object.getImageUrls() != null ? object.getImageUrls() : List.of());
//        needDelImgUrls.removeAll(dto.getImageUrls());

        List<String> needDelMediaIds = new ArrayList<>(object.getMediaIds() != null ? object.getMediaIds() : List.of());
        needDelMediaIds.removeAll(keepMedias.stream().map(Media::getId).toList());

        List<Media> needDelMedias = mediaRepository.findAllById(needDelMediaIds);

//        if(!needDelImgUrls.isEmpty())
//            needDelImgUrls.forEach(url -> {
//                fileService.delete(url, EFileType.TYPE_IMG);
//            });
//
//        if(!needDelVidUrls.isEmpty())
//            needDelVidUrls.forEach(url -> {
//                fileService.delete(url, EFileType.TYPE_VIDEO);
//            });

        if (!needDelMedias.isEmpty())
            needDelMedias.forEach(media -> {
                if (EFileType.TYPE_VIDEO.equals(media.getType()))
                    fileService.delete(media.getUrl(), EFileType.TYPE_VIDEO);

                else if (EFileType.TYPE_IMG.equals(media.getType()))
                    fileService.delete(media.getUrl(), EFileType.TYPE_IMG);
            });

        mediaRepository.deleteAll(needDelMedias);

        List<Media> needAddMedias = dto.getMedias().stream()
                .filter(media -> media.getId() == null || media.getId().isEmpty())
                .toList();

        List<Media> addedMedias = mediaRepository.saveAll(needAddMedias);

//        object.setVideoUrls(dto.getVideoUrls());
//        object.setImageUrls(dto.getImageUrls());
        object.setMediaIds(Stream.concat(
                keepMedias.stream().map(Media::getId),
                addedMedias.stream().map(Media::getId)
        ).toList());

        if (!object.getPrivacy().equals(dto.getPrivacy())) {
            object.setPrivacy(dto.getPrivacy());
        }

        object.setUpdatedAt(LocalDateTime.now());
    }
}
