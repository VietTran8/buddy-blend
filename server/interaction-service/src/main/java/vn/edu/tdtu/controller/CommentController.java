package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.requests.AddCommentRequest;
import vn.edu.tdtu.dto.requests.UpdateCommentRequest;
import vn.edu.tdtu.service.interfaces.CommentService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_INTERACTION + "/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentsService;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody AddCommentRequest comment) {
        ResponseVM<?> response = commentsService.addComment(comment);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") String id,
                                           @RequestBody UpdateCommentRequest comment) {
        ResponseVM<?> response = commentsService.updateComment(id, comment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") String id) {
        ResponseVM<?> response = commentsService.deleteComment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findCommentById(@PathVariable("id") String id) {
        ResponseVM<?> response = commentsService.findCommentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/post/{id}")
    public ResponseEntity<?> countComments(@PathVariable("id") String id) {
        ResponseVM<?> response = commentsService.countCommentByPostId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> findAllComments(@RequestParam(name = "postId", required = false) String postId) {
        if (postId != null) {
            ResponseVM<?> response = commentsService.findCommentsByPostId(postId);
            return ResponseEntity.ok(response);
        }

        ResponseVM<?> response = commentsService.findAllComments();
        return ResponseEntity.ok(response);
    }
}
