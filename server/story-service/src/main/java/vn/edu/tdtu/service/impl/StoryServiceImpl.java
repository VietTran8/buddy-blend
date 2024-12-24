package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.edu.tdtu.dto.response.LatestStoriesResponse;
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
import vn.edu.tdtu.repository.ViewerRepository;
import vn.edu.tdtu.service.interfaces.StoryService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {
    private final StoryRepository storyRepository;
    private final ViewerRepository viewerRepository;
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
        story.setThumbnailUrl(payload.getThumbnailUrl());
        story.setMediaType(payload.getMediaType());
        story.setFont(payload.getFont());
        story.setContent(payload.getContent());
        story.setBackground(payload.getBackground());
        story.setStoryType(payload.getStoryType());

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

        Map<String, User> userMap = userService.getUsersByIds(
                        accessToken,
                        viewers.stream().map(Viewer::getUserId).toList()
                )
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        List<ViewerResponse> viewerResponses = viewerMapper.mapToDtos(viewers, userMap);

        return new ResDTO<>(
                Message.VIEWER_NOT_FOUND_MSG,
                viewerResponses,
                HttpServletResponse.SC_OK
        );
    }

    public ResDTO<?> getUserStory(String accessTokenHeader, String userId) {
        User foundUser = userService.getUserById(accessTokenHeader, userId);

        List<User> friends = userService.getUserFriends(accessTokenHeader);

        if(foundUser == null)
            throw new BadRequestException(Message.USER_NOT_FOUND_MSG);

        List<StoryResponse> stories = storyRepository.findUserStory(
                        userId,
                        SecurityContextUtils.getUserId(),
                        friends.stream().map(User::getId).toList(),
                        LocalDateTime.now()
                )
                .stream().map(story -> {
                    StoryResponse storyResponse = storyMapper.mapToDto(story);
                    storyResponse.setUser(foundUser);

                    return storyResponse;
                })
                .toList();

        ResDTO<List<StoryResponse>> response = new ResDTO<>();

        response.setData(stories);
        response.setMessage(Message.STORY_FETCHED_MSG);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    public ResDTO<?> getStories(String accessToken) {
        String userId = SecurityContextUtils.getUserId();
        List<User> friends = userService.getUserFriends(accessToken);

        List<Story> stories = storyRepository.findStories(
                LocalDateTime.now(),
                userId,
                friends.stream().map(User::getId).toList()
        );

        Map<String, User> userMap = stories
                .stream()
                .map(Story::getUserId)
                .distinct()
                .collect(Collectors.toMap(
                        ownId -> ownId, anotherOwnId -> userService.getUserById(accessToken, anotherOwnId)
                ));

        List<LatestStoriesResponse> storyResponses = stories.stream()
                .collect(Collectors.groupingBy(Story::getUserId))
                .values()
                .stream()
                .map(storyList -> storyList.stream()
                        .max(Comparator.comparing(Story::getCreatedAt))
                        .map(latestStory -> {
                            StoryResponse response = storyMapper.mapToDto(latestStory);
                            response.setUser(userMap.get(response.getUserId()));

                            LatestStoriesResponse latestStoriesResponse = new LatestStoriesResponse();
                            latestStoriesResponse.setLatestStory(response);
                            latestStoriesResponse.setUser(response.getUser());
                            latestStoriesResponse.setSeenAll(!latestStory.getUserId().equals(userId) && viewerRepository.countByUserIdAndStoryIn(userId, storyList) == storyList.size());

                            return latestStoriesResponse;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return new ResDTO<>(
                Message.STORY_FETCHED_MSG,
                storyResponses,
                HttpServletResponse.SC_OK
        );
    }
}
