package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.edu.tdtu.service.interfaces.CommentReactionService;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping("/api/v1/reacts")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;
    private final CommentReactionService commentReactionService;

    @PostMapping()
    public ResponseEntity<?> doReact(@RequestHeader("Authorization") String token,
                                     @RequestBody DoReactRequest request) {
        ResponseVM<?> response = reactionService.doReaction(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> findByPost(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam("postId") String postId) {
        ResponseVM<?> response = reactionService.getReactsByPostId(token, postId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/cmt")
    public ResponseEntity<?> doCmtReact(@RequestBody DoCommentReactRequest request) {
        ResponseVM<?> response = commentReactionService.doReact(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/cmt")
    public ResponseEntity<?> findByCmt(@RequestHeader(name = "Authorization") String token,
                                       @RequestParam("cmtId") String cmtId) {
        ResponseVM<?> response = commentReactionService.getReactsByCmtId(token, cmtId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
