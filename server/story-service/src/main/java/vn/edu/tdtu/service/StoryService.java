package vn.edu.tdtu.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.edu.tdtu.dto.response.StoryIdResponse;
import vn.edu.tdtu.dto.response.StoryResponse;
import vn.edu.tdtu.dto.response.ViewerResponse;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.StoryMapper;
import vn.edu.tdtu.mapper.ViewerMapper;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.utils.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final UserService userService;
    private final StoryMapper storyMapper;
    private final ViewerMapper viewerMapper;

    public ResDTO<StoryIdResponse> createStory(CreateStoryRequest payload) {
        String userId = SecurityContextUtils.getUserId();
        Story story = new Story();

        story.setCreatedAt(LocalDateTime.now());
        story.setExpiredAt(LocalDateTime.now().plusHours(24L));
        story.setPrivacy(payload.getPrivacy());
        story.setMediaUrl(payload.getMediaUrl());
        story.setUserId(userId);

        storyRepository.save(story);

        return new ResDTO<>(
                Message.STORY_CREATED_MSG,
                new StoryIdResponse(story.getId()),
                HttpServletResponse.SC_CREATED
        );
    }

    public ResDTO<StoryIdResponse> deleteStory(String storyId) {
        String userId = SecurityContextUtils.getUserId();
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(Message.STORY_NOT_FOUND_MSG));

        if(!foundStory.getUserId().equals(userId))
            throw new BadRequestException(Message.STORY_CAN_NOT_DELETE_OTHER_MSG);

        storyRepository.deleteById(foundStory.getId());

        return new ResDTO<>(
                Message.STORY_DELETED_MSG,
                new StoryIdResponse(storyId),
                HttpServletResponse.SC_CREATED
        );
    }

    public ResDTO<StoryIdResponse> countView(String storyId) {
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(Message.STORY_NOT_FOUND_MSG));

        String userId = SecurityContextUtils.getUserId();

        List<Viewer> viewers = foundStory.getViewers();

        if(viewers.stream().noneMatch(viewer -> viewer.getUserId().equals(userId)) && !foundStory.getUserId().equals(userId)){
            Viewer newViewer = new Viewer();
            newViewer.setStory(foundStory);
            newViewer.setViewedAt(LocalDateTime.now());
            newViewer.setUserId(userId);

            viewers.add(newViewer);

            storyRepository.save(foundStory);
        }

        return new ResDTO<>(
                Message.VIEWER_COUNTED_MSG,
                new StoryIdResponse(foundStory.getId()),
                HttpServletResponse.SC_OK
        );
    }

    public ResDTO<?> getViewers(String accessToken, String storyId) {
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(Message.STORY_FETCHED_MSG));

        List<Viewer> viewers = foundStory.getViewers();
        List<User> userViewers = userService.getUsersByIds(
                accessToken,
                viewers.stream().map(Viewer::getUserId).toList()
        );

        List<ViewerResponse> viewerResponses = viewerMapper.mapToDtos(viewers, userViewers);

        return new ResDTO<>(
                Message.VIEWER_NOT_FOUND_MSG,
                viewerResponses,
                HttpServletResponse.SC_OK
        );
    }

    public ResDTO<?> getStories(String accessToken) {
        String userId = SecurityContextUtils.getUserId();
        List<User> friends = userService.getUserFriends(accessToken);

        List<Story> stories = storyRepository.findStories(
                LocalDateTime.now(),
                userId,
                friends.stream().map(User::getId).toList()
        );

        Map<String, List<StoryResponse>> storyResponses = stories.stream()
                .map(story -> storyMapper.mapToDto(accessToken, story))
                .collect(Collectors.groupingBy(StoryResponse::getUserId));

        return new ResDTO<>(
                Message.STORY_FETCHED_MSG,
                storyResponses,
                HttpServletResponse.SC_OK
        );
    }
}
