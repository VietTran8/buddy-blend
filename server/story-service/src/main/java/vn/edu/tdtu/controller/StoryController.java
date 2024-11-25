package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.edu.tdtu.dto.request.DoReactRequest;
import vn.edu.tdtu.service.ReactionService;
import vn.edu.tdtu.service.StoryService;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final ReactionService reactionService;

    @GetMapping
    public ResponseEntity<?> getStories(@RequestHeader("Authorization") String accessTokenHeader) {
        ResDTO<?> response = storyService.getStories(accessTokenHeader);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserStory(
            @RequestHeader("Authorization") String accessTokenHeader,
            @PathVariable("userId") String userId
    ) {
        ResDTO<?> response = storyService.getUserStory(accessTokenHeader, userId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/views/{storyId}")
    public ResponseEntity<?> getViews(
            @RequestHeader("Authorization") String accessTokenHeader,
            @PathVariable("storyId") String storyId
    ) {
        ResDTO<?> response = storyService.getViewers(accessTokenHeader, storyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createStory(@RequestBody CreateStoryRequest payload) {
        ResDTO<?> response = storyService.createStory(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/views/count/{storyId}")
    public ResponseEntity<?> countStoryView(@PathVariable("storyId") String storyId) {
        ResDTO<?> response = storyService.countView(storyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/react")
    public ResponseEntity<?> doReaction(@RequestHeader("Authorization") String tokenHeader, @RequestBody DoReactRequest payload) {
        ResDTO<?> response = reactionService.doReact(tokenHeader, payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable("storyId") String storyId) {
        ResDTO<?> response = storyService.deleteStory(storyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
