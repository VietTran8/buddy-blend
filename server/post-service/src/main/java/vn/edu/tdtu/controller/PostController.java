package vn.edu.tdtu.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.request.CreatePostRequest;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.request.SharePostRequest;
import vn.edu.tdtu.dto.request.UpdatePostContentRequest;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.edu.tdtu.service.intefaces.SavePostService;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final SavePostService savePostService;

    @GetMapping("/news-feed")
    public ResponseEntity<?> getNewFeeds(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("startTime") String startTime
    ) {
        ResponseVM<?> response = postService.getNewsFeed(page, size, startTime);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> post(@RequestBody CreatePostRequest requestBody) {
        ResponseVM<?> response = postService.savePost(requestBody);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        ResponseVM<?> response = postService.findPostRespById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/find-all")
    public ResponseEntity<?> findByIds(@RequestBody FindByIdsReq req) {
        ResponseVM<?> response = postService.findPostRespByIds(req);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/save/{postId}")
    public ResponseEntity<?> handleSavePost(@PathVariable("postId") String postId) {
        ResponseVM<?> response = savePostService.handleSavePost(postId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/save/posts")
    public ResponseEntity<?> getUserSavedPosts() {
        ResponseVM<?> response = savePostService.getUserSavedPost();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserIdByPost(@PathVariable("id") String id) {
        ResponseVM<?> response = postService.getUserIdByPostId(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            
            @RequestParam("key") String key) {
        ResponseVM<?> response = postService.findByContentContaining(key);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> update(
            
            @RequestBody UpdatePostContentRequest request
    ) {
        ResponseVM<?> response = postService.updatePostContent(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/delete/{id}")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        ResponseVM<?> response = postService.deletePost(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/share")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> sharePost(@RequestBody SharePostRequest request) {
        ResponseVM<?> response = postService.sharePost(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupPosts(
            @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int limit
    ) {
        ResponseVM<?> response = postService.getGroupPosts(groupId, page, limit);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/detached/{postId}")
    public ResponseEntity<?> getDetachedPost(@PathVariable("postId") String postId) {
        ResponseVM<PostDTO> response = postService.findDetachedPost(postId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getMyPost(@RequestParam(name = "userId", required = false, defaultValue = "") String userId,
                                       @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        ResponseVM<?> response = postService.findUserPosts(userId, page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
