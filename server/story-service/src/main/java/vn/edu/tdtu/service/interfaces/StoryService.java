package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.CreateStoryRequest;
import vn.tdtu.common.viewmodel.IDResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface StoryService {
    ResponseVM<IDResponseVM> createStory(CreateStoryRequest payload);

    ResponseVM<IDResponseVM> deleteStory(String storyId);

    ResponseVM<IDResponseVM> countView(String storyId);

    ResponseVM<?> getViewers(String storyId);

    ResponseVM<?> getUserStory(String userId);

    ResponseVM<?> getStories();
}
