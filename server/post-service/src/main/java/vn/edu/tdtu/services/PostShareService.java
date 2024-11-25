package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.response.PostResponse;
import vn.edu.tdtu.mappers.response.SharePostResponseMapper;
import vn.edu.tdtu.models.PostShare;
import vn.edu.tdtu.repositories.CustomPostShareRepository;
import vn.edu.tdtu.repositories.PostRepository;
import vn.edu.tdtu.repositories.PostShareRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostShareService {
    private final PostShareRepository postShareRepository;
    private final CustomPostShareRepository customPostShareRepository;
    private final PostRepository postRepository;
    private final SharePostResponseMapper sharePostResponseMapper;

    public List<PostShare> findSharedPostByUserId(String userId){
        return postShareRepository.findBySharedUserId(userId);
    }

    public List<PostShare> findByIds(List<String> ids) {
        return postShareRepository.findByIdIn(ids);
    }

    public PostShare save(PostShare postShare){
        return postShareRepository.save(postShare);
    }

    public List<PostShare> findSharedPostByFriendIds(List<String> friendIds, String userId, LocalDateTime startTime){
        return customPostShareRepository.findSharedPostIdsByFriends(friendIds, userId, startTime);
    }

    public PostResponse findPostRespById(String token, String id) {
        PostShare postShare = postShareRepository.findById(id).orElse(null);

        if(postShare != null) {
            return sharePostResponseMapper.mapToPostResponse(token, postShare);
        }

        return null;
    }

    public List<PostResponse> searchSharePost(String token, String content){
        return postShareRepository.findByContent(content).stream()
                .map(post -> sharePostResponseMapper.mapToPostResponse(token, post))
                .filter(Objects::nonNull)
                .toList();
    }

    public Optional<PostShare> findById(String id){
        return postShareRepository.findById(id);
    }

    public PostResponse mapToPostResponse(String token, PostShare postShare){
        return sharePostResponseMapper.mapToPostResponse(token, postShare);
    }

    public void delete(PostShare postShare) {
        postShareRepository.delete(postShare);
    }
}
