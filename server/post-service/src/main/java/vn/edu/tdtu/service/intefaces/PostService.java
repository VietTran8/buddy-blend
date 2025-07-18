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

    ResponseVM<?> findPostRespById(String postId);

    ResponseVM<List<PostDTO>> findPostRespByIds(FindByIdsReq req);

    ResponseVM<?> getUserIdByPostId(String postId);

    PostDTO mapToPostDTO(Post post);

    ResponseVM<?> getGroupPosts(String groupId, int page, int limit);

    ResponseVM<?> getNewsFeed(int page, int size, String startTime);

    ResponseVM<?> findByContentContaining(String key);

    ResponseVM<?> savePost(CreatePostRequest postRequest);

    ResponseVM<?> updatePostContent(UpdatePostContentRequest request);

    ResponseVM<?> deletePost(String postId);

    ResponseVM<?> sharePost(SharePostRequest request);

    ResponseVM<PostDTO> findDetachedPost(String postId);

    ResponseVM<PaginationResponseVM<PostDTO>> findUserPosts(String uId, int page, int size);
}