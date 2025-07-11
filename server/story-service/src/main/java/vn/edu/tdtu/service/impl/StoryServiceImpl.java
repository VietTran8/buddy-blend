package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.edu.tdtu.dto.response.LatestStoriesResponse;
import vn.edu.tdtu.dto.response.StoryResponse;
import vn.edu.tdtu.dto.response.ViewerResponse;
import vn.edu.tdtu.mapper.StoryMapper;
import vn.edu.tdtu.mapper.ViewerMapper;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.repository.ViewerRepository;
import vn.edu.tdtu.service.interfaces.StoryService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.IDResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

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
    public ResponseVM<IDResponseVM> createStory(CreateStoryRequest payload) {
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

        return new ResponseVM<>(
                MessageCode.Story.STORY_CREATED,
                new IDResponseVM(story.getId()),
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResponseVM<IDResponseVM> deleteStory(String storyId) {
        String userId = SecurityContextUtils.getUserId();
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Story.STORY_NOT_FOUND));

        if (!foundStory.getUserId().equals(userId))
            throw new BadRequestException(MessageCode.Story.STORY_CAN_NOT_DELETE_OTHER);

        storyRepository.deleteById(foundStory.getId());

        return new ResponseVM<>(
                MessageCode.Story.STORY_DELETED,
                new IDResponseVM(storyId),
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResponseVM<IDResponseVM> countView(String storyId) {
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Story.STORY_NOT_FOUND));

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

        return new ResponseVM<>(
                MessageCode.Story.VIEWER_COUNTED,
                new IDResponseVM(foundStory.getId()),
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> getViewers(String accessToken, String storyId) {
        Story foundStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Story.STORY_FETCHED));

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

        return new ResponseVM<>(
                MessageCode.Story.VIEWER_NOT_FOUND,
                viewerResponses,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> getUserStory(String accessTokenHeader, String userId) {
        UserDTO foundUser = userService.getUserById(accessTokenHeader, userId);

        List<UserDTO> friends = userService.getUserFriends(accessTokenHeader);

        if (foundUser == null)
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND);

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

        ResponseVM<List<StoryResponse>> response = new ResponseVM<>();

        response.setData(stories);
        response.setMessage(MessageCode.Story.STORY_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    @Override
    public ResponseVM<?> getStories(String accessToken) {
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

        return new ResponseVM<>(
                MessageCode.Story.STORY_FETCHED,
                storyResponses,
                HttpServletResponse.SC_OK
        );
    }
}
