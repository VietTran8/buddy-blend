package vn.edu.tdtu.mappers.request;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.request.CreatePostRequest;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.utils.StringUtils;

import java.time.LocalDateTime;

@Component
public class PostPostRequestMapper {

    public Post mapToObject(CreatePostRequest request){
        Post post = new Post();

        post.setContent(request.getContent());
        post.setActive(true);
        post.setPrivacy(request.getPrivacy());
        post.setImageUrls(request.getImageUrls());
        post.setVideoUrls(request.getVideoUrls());
        post.setType(request.getType());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setNormalizedContent(StringUtils.toSlug(post.getContent()));

        return post;
    }
}