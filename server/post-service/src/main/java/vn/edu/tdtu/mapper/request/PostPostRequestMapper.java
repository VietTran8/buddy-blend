package vn.edu.tdtu.mapper.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.CreatePostRequest;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class PostPostRequestMapper {
    private final MediaRepository mediaRepository;

    public Post mapToObject(CreatePostRequest request){
        Post post = new Post();

        post.setContent(request.getContent());
        post.setActive(true);
        post.setPrivacy(request.getPrivacy());
        post.setType(request.getType());
        post.setDetached(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setBackground(request.getBackground());
        post.setGroupId(request.getGroupId());
        post.setUpdatedAt(LocalDateTime.now());
        post.setNormalizedContent(StringUtils.toSlug(post.getContent()));

        List<String> savedMediaIds = mediaRepository
                .saveAll(request.getMedias())
                .stream().map(Media::getId)
                .toList();

        post.setMediaIds(savedMediaIds);

        return post;
    }
}
