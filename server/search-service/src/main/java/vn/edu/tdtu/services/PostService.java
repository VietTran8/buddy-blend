package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.request.FindByIdsReq;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.es.SyncPost;
import vn.edu.tdtu.repository.EsPostRepository;
import vn.edu.tdtu.repository.httpclient.PostClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostClient postClient;
    private final EsPostRepository postRepository;

    public void savePost(SyncPost post) {
        postRepository.save(post);
    }

    public void updatePost(SyncPost post) {
        postRepository.findById(post.getId()).ifPresentOrElse(
                foundPost -> {
                    foundPost.setContent(post.getContent());
                    postRepository.save(post);
                }, () -> {
                    throw new BadRequestException("Post not found with id: " + post.getId());
                }
        );
    }
    
    public void deletePost(SyncPost post) {
        postRepository.deleteById(post.getId());
    }
    
    public List<Post> findByContentContaining(String token, String key) {
        List<String> matchPostIds = postRepository.findByContent(key).stream().map(SyncPost::getId).toList();

        return postClient
                .findAll(token, new FindByIdsReq(matchPostIds))
                .getData();
    }
}
