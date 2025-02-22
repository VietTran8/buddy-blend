package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreatePostRequest;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.request.SharePostRequest;
import vn.edu.tdtu.dto.request.UpdatePostContentRequest;
import vn.edu.tdtu.dto.response.PaginationResponse;
import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.model.Post;

import java.util.List;

public interface PostService {
    public Post findPostById(String postId);
    public ResDTO<?> findPostRespById(String token, String postId);
    public ResDTO<List<PostResponse>> findPostRespByIds(String token, FindByIdsReq req);
    public ResDTO<?> getUserIdByPostId(String postId);
    public PostResponse mapToPostResponse (String token, Post post);
    public ResDTO<?> getGroupPosts(String token, String groupId, int page, int limit);
    public ResDTO<?> getNewsFeed(String token, int page, int size, String startTime);
    public ResDTO<?> findByContentContaining(String token, String key);
    public ResDTO<?> savePost(String token, CreatePostRequest postRequest);
    public ResDTO<?> updatePostContent(String token, UpdatePostContentRequest request);
    public ResDTO<?> deletePost(String postId);
    public ResDTO<?> sharePost(String token, SharePostRequest request);
    public ResDTO<PostResponse> findDetachedPost(String token, String postId);
    public ResDTO<PaginationResponse<PostResponse>> findUserPosts(String token, String uId, int page, int size);
}
