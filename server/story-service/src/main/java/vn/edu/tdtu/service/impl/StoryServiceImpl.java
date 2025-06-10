package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
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
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.repository.ViewerRepository;
import vn.edu.tdtu.service.interfaces.StoryService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;
import vn.tdtu.common.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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

    @Override
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
                MessageCode.STORY_CREATED,
                new StoryIdResponse(story.getId()),
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResDTO<StoryIdResponse> deleteStory(String storyId) {
        String userId = SecurityContextUtils.getUserId();
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(MessageCode.STORY_NOT_FOUND));

        if (!foundStory.getUserId().equals(userId))
            throw new BadRequestException(MessageCode.STORY_CAN_NOT_DELETE_OTHER);

        storyRepository.deleteById(foundStory.getId());

        return new ResDTO<>(
                MessageCode.STORY_DELETED,
                new StoryIdResponse(storyId),
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResDTO<StoryIdResponse> countView(String storyId) {
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(MessageCode.STORY_NOT_FOUND));

        String userId = SecurityContextUtils.getUserId();

        List<Viewer> viewers = foundStory.getViewers();

        if (viewers.stream().noneMatch(viewer -> viewer.getUserId().equals(userId)) && !foundStory.getUserId().equals(userId)) {
            Viewer newViewer = new Viewer();
            newViewer.setStory(foundStory);
            newViewer.setViewedAt(LocalDateTime.now());
            newViewer.setUserId(userId);

            viewers.add(newViewer);

            storyRepository.save(foundStory);
        }

        return new ResDTO<>(
                MessageCode.VIEWER_COUNTED,
                new StoryIdResponse(foundStory.getId()),
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResDTO<?> getViewers(String accessToken, String storyId) {
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(MessageCode.STORY_FETCHED));

        List<Viewer> viewers = foundStory.getViewers();

        Map<String, UserDTO> userMap = userService.getUsersByIds(
                        accessToken,
                        viewers.stream().map(Viewer::getUserId).toList()
                )
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        UserDTO::getId,
                        Function.identity()
                ));

        List<ViewerResponse> viewerResponses = viewerMapper.mapToDtos(viewers, userMap);

        return new ResDTO<>(
                MessageCode.VIEWER_NOT_FOUND,
                viewerResponses,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResDTO<?> getUserStory(String accessTokenHeader, String userId) {
        UserDTO foundUser = userService.getUserById(accessTokenHeader, userId);

        List<UserDTO> friends = userService.getUserFriends(accessTokenHeader);

        if (foundUser == null)
            throw new BadRequestException(MessageCode.USER_NOT_FOUND_MSG);

        List<StoryResponse> stories = storyRepository.findUserStory(
                        userId,
                        SecurityContextUtils.getUserId(),
                        friends.stream().map(UserDTO::getId).toList(),
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
        response.setMessage(MessageCode.STORY_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    @Override
    public ResDTO<?> getStories(String accessToken) {
        String userId = SecurityContextUtils.getUserId();
        List<UserDTO> friends = userService.getUserFriends(accessToken);

        List<Story> stories = storyRepository.findStories(
                LocalDateTime.now(),
                userId,
                friends.stream().map(UserDTO::getId).toList()
        );

        Map<String, UserDTO> userMap = stories
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
                MessageCode.STORY_FETCHED,
                storyResponses,
                HttpServletResponse.SC_OK
        );
    }
}
