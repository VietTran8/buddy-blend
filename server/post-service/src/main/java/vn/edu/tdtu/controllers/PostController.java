package vn.edu.tdtu.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.CreatePostRequest;
import vn.edu.tdtu.dtos.request.FindByIdsReq;
import vn.edu.tdtu.dtos.request.SharePostRequest;
import vn.edu.tdtu.dtos.request.UpdatePostContentRequest;
import vn.edu.tdtu.services.PostService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/news-feed")
    public ResponseEntity<?> getNewFeeds(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("startTime") String startTime
    ){
        ResDTO<?> response = postService.getNewsFeed(token, page, size, startTime);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> post(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody CreatePostRequest requestBody
    ){
        ResDTO<?> response = postService.savePost(token, requestBody);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@RequestHeader("Authorization") String token,
                                      @PathVariable("id") String id){
        ResDTO<?> response = postService.findPostRespById(token, id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/find-all")
    public ResponseEntity<?> findByIds(@RequestHeader("Authorization") String token,
                                       @RequestBody FindByIdsReq req){
        ResDTO<?> response = postService.findPostRespByIds(token, req);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserIdByPost(@PathVariable("id") String id){
        ResDTO<?> response = postService.getUserIdByPostId(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestHeader(name = "Authorization", required = false, defaultValue = "") String token,
            @RequestParam("key") String key){
        ResDTO<?> response = postService.findByContentContaining(token, key);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> update(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdatePostContentRequest request
            ){
        ResDTO<?> response = postService.updatePostContent(token ,request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/delete/{id}")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") String id
    ){
        ResDTO<?> response = postService.deletePost(token, id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/share")
    @CacheEvict(cacheNames = "news-feed", allEntries = true)
    public ResponseEntity<?> sharePost(
            @RequestHeader("Authorization") String token,
            @RequestBody SharePostRequest request
            ){
        ResDTO<?> response = postService.sharePost(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupPosts(
            @RequestHeader("Authorization") String token,
            @PathVariable("groupId") String groupId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int limit
    ){
        ResDTO<?> response = postService.getGroupPosts(token, groupId, page, limit);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> getMyPost(@RequestHeader("Authorization") String token,
                                       @RequestParam(name = "userId", required = false, defaultValue = "") String userId){
        ResDTO<?> response = postService.findUserPosts(token, userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
