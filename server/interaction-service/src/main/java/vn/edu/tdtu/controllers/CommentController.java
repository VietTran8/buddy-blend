package vn.edu.tdtu.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.requests.AddCommentRequest;
import vn.edu.tdtu.dtos.requests.UpdateCommentRequest;
import vn.edu.tdtu.services.CommentsService;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token,
                                        @RequestBody AddCommentRequest comment) {
        ResDTO<?> response = commentsService.addComment(token, comment);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("id") String id,
                                           @RequestBody UpdateCommentRequest comment) {
        ResDTO<?> response = commentsService.updateComment(token, id, comment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("id") String id) {
        ResDTO<?> response = commentsService.deleteComment(token, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findCommentById(@RequestHeader(name = "Authorization") String token,
                                             @PathVariable("id") String id) {
        ResDTO<?> response = commentsService.findCommentById(token, id);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<?> findAllComments(@RequestHeader(name = "Authorization") String token,
                                             @RequestParam(name = "postId", required = false) String postId) {
        if (postId != null) {
            ResDTO<?> response = commentsService.findCommentsByPostId(token, postId);
            return ResponseEntity.ok(response);
        } else {
            ResDTO<?> response = commentsService.findAllComments(token);
            return ResponseEntity.ok(response);
        }
    }
}
