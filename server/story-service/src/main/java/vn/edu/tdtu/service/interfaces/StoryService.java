package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.edu.tdtu.dto.response.StoryIdResponse;

public interface StoryService {
    public ResDTO<StoryIdResponse> createStory(CreateStoryRequest payload);
    public ResDTO<StoryIdResponse> deleteStory(String storyId);
    public ResDTO<StoryIdResponse> countView(String storyId);
    public ResDTO<?> getViewers(String accessToken, String storyId);
    public ResDTO<?> getUserStory(String accessTokenHeader, String userId);
    public ResDTO<?> getStories(String accessToken);
}
