package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.model.PostShare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostShareService {
    public List<PostShare> findSharedPostByUserId(String userId);
    public List<PostShare> findByIds(List<String> ids);
    public PostShare save(PostShare postShare);
    public List<PostShare> findSharedPostByFriendIds(List<String> friendIds, String userId, LocalDateTime startTime);
    public PostResponse findPostRespById(String token, String id);
    public List<PostResponse> searchSharePost(String token, String content);

    public Optional<PostShare> findById(String id);
    public PostResponse mapToPostResponse(String token, PostShare postShare);

    public void delete(PostShare postShare);
}
