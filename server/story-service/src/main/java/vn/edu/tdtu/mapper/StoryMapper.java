package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.StoryResponse;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.service.UserService;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StoryMapper {
    private final UserService userService;
    private final StoryRepository storyRepository;

    public StoryResponse mapToDto(String accessToken, Story story) {
        String userId = SecurityContextUtils.getUserId();
        StoryResponse storyResponse = new StoryResponse();

        storyResponse.setCreatedAt(story.getCreatedAt());
        storyResponse.setExpiredAt(story.getExpiredAt());
        storyResponse.setId(story.getId());
        storyResponse.setPrivacy(story.getPrivacy());
        storyResponse.setMediaUrl(story.getMediaUrl());
        storyResponse.setStoryCount(storyRepository.countByExpiredAtAfterAndUserId(LocalDateTime.now(), userId));
        storyResponse.setMine(story.getUserId().equals(userId));
        storyResponse.setUser(userService.getUserById(accessToken, story.getUserId()));
        storyResponse.setUserId(story.getUserId());

        return storyResponse;
    }
}
