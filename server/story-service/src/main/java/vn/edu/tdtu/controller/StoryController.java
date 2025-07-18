package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.edu.tdtu.dto.request.DoReactRequest;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.edu.tdtu.service.interfaces.StoryService;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final ReactionService reactionService;

    @GetMapping
    public ResponseEntity<?> getStories() {
        ResponseVM<?> response = storyService.getStories();

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/by-id/{userId}")
    public ResponseEntity<?> getUserStory(
            @PathVariable("userId") String userId
    ) {
        ResponseVM<?> response = storyService.getUserStory(userId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/views/{storyId}")
    public ResponseEntity<?> getViews(
            @PathVariable("storyId") String storyId
    ) {
        ResponseVM<?> response = storyService.getViewers(storyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createStory(@RequestBody CreateStoryRequest payload) {
        ResponseVM<?> response = storyService.createStory(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/views/count/{storyId}")
    public ResponseEntity<?> countStoryView(@PathVariable("storyId") String storyId) {
        ResponseVM<?> response = storyService.countView(storyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/react")
    public ResponseEntity<?> doReaction(@RequestBody DoReactRequest payload) {
        ResponseVM<?> response = reactionService.doReact(payload);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable("storyId") String storyId) {
        ResponseVM<?> response = storyService.deleteStory(storyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
