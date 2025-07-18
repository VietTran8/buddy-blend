package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.edu.tdtu.service.interfaces.CommentReactionService;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_INTERACTION + "/reacts")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;
    private final CommentReactionService commentReactionService;

    @PostMapping()
    public ResponseEntity<?> doReact(@RequestBody DoReactRequest request) {
        ResponseVM<?> response = reactionService.doReaction(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> findByPost(@RequestParam("postId") String postId) {
        ResponseVM<?> response = reactionService.getReactsByPostId(postId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/cmt")
    public ResponseEntity<?> doCmtReact(@RequestBody DoCommentReactRequest request) {
        ResponseVM<?> response = commentReactionService.doReact(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/cmt")
    public ResponseEntity<?> findByCmt(@RequestParam("cmtId") String cmtId) {
        ResponseVM<?> response = commentReactionService.getReactsByCmtId(cmtId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
