package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.StoryResponse;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StoryMapper {
    private final StoryRepository storyRepository;

    public StoryResponse mapToDto(Story story) {
        if(story == null)
            return new StoryResponse();

        String userId = SecurityContextUtils.getUserId();
        StoryResponse storyResponse = new StoryResponse();

        List<Viewer> storyViewers = story.getViewers();

        storyResponse.setThumbnailUrl(story.getThumbnailUrl());
        storyResponse.setMediaType(story.getMediaType());
        storyResponse.setCreatedAt(story.getCreatedAt());
        storyResponse.setExpiredAt(story.getExpiredAt());
        storyResponse.setId(story.getId());
        storyResponse.setPrivacy(story.getPrivacy());
        storyResponse.setMediaUrl(story.getMediaUrl());
        storyResponse.setStoryCount(storyRepository.countByExpiredAtAfterAndUserId(LocalDateTime.now(), userId));
        storyResponse.setMine(story.getUserId().equals(userId));
        storyResponse.setUserId(story.getUserId());
        storyResponse.setStoryType(story.getStoryType());
        storyResponse.setBackground(story.getBackground());
        storyResponse.setContent(story.getContent());
        storyResponse.setFont(story.getFont());
        storyResponse.setViewCount(storyResponse.isMine() ? (long) storyViewers.size() : null);
        storyResponse.setSeen(storyViewers.stream().anyMatch(viewer -> viewer.getUserId().equals(userId)));

        return storyResponse;
    }
}
