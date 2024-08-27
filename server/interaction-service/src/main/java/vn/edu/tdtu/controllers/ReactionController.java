package vn.edu.tdtu.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.requests.DoCommentReactRequest;
import vn.edu.tdtu.dtos.requests.DoReactRequest;
import vn.edu.tdtu.dtos.response.InteractNotification;
import vn.edu.tdtu.services.CommentReactionService;
import vn.edu.tdtu.services.ReactionService;

@RestController
@RequestMapping("/api/v1/reacts")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;
    private final CommentReactionService commentReactionService;

    @PostMapping()
    public ResponseEntity<?> doReact(@RequestHeader("Authorization") String token,
                                     @RequestBody DoReactRequest request){
        ResDTO<?> response = reactionService.doReaction(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> findByPost(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam("postId") String postId){
        ResDTO<?> response = reactionService.getReactsByPostId(token, postId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/cmt")
    public ResponseEntity<?> doCmtReact(@RequestHeader("Authorization") String token,
                                     @RequestBody DoCommentReactRequest request){
        ResDTO<?> response = commentReactionService.doReact(token, request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
