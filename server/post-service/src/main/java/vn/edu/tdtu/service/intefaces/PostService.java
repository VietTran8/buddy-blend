package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.request.CreatePostRequest;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.request.SharePostRequest;
import vn.edu.tdtu.dto.request.UpdatePostContentRequest;
import vn.edu.tdtu.model.Post;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.viewmodel.PaginationResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

public interface PostService {
    Post findPostById(String postId);

    ResponseVM<?> findPostRespById(String token, String postId);

    ResponseVM<List<PostDTO>> findPostRespByIds(String token, FindByIdsReq req);

    ResponseVM<?> getUserIdByPostId(String postId);

    PostDTO mapToPostDTO(String token, Post post);

    ResponseVM<?> getGroupPosts(String token, String groupId, int page, int limit);

    ResponseVM<?> getNewsFeed(String token, int page, int size, String startTime);

    ResponseVM<?> findByContentContaining(String token, String key);

    ResponseVM<?> savePost(String token, CreatePostRequest postRequest);

    ResponseVM<?> updatePostContent(String token, UpdatePostContentRequest request);

    ResponseVM<?> deletePost(String postId);

    ResponseVM<?> sharePost(String token, SharePostRequest request);

    ResponseVM<PostDTO> findDetachedPost(String token, String postId);

    ResponseVM<PaginationResponseVM<PostDTO>> findUserPosts(String token, String uId, int page, int size);
}