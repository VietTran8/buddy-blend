package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.DoReactRequest;
import vn.edu.tdtu.dto.response.DoReactResponse;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.enums.ENotificationType;
import vn.edu.tdtu.model.Reaction;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.repository.ViewerRepository;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final StoryRepository storyRepository;
    private final ViewerRepository viewerRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final UserService userService;

    @Override
    public ResponseVM<DoReactResponse> doReact(DoReactRequest payload) {
        String userId = SecurityContextUtils.getUserId();

        Story foundStory = storyRepository.findById(payload.getStoryId())
                .orElseThrow(() -> new BadRequestException(MessageCode.Story.STORY_NOT_FOUND));

        if (foundStory.getUserId().equals(userId))
            throw new BadRequestException(MessageCode.Story.STORY_CAN_NOT_SELF_REACT);

        Viewer foundViewer = viewerRepository.findTopByStoryAndUserId(foundStory, userId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Story.VIEWER_NOT_FOUND));

        foundViewer.getReactions().add(newReaction(foundViewer, userId, payload));

        viewerRepository.save(foundViewer);

        sendNotification(payload, userId, foundStory);

        return new ResponseVM<>(
                MessageCode.Story.REACTION_CREATED,
                new DoReactResponse(payload),
                HttpServletResponse.SC_CREATED
        );
    }

    private void sendNotification(DoReactRequest payload, String userId, Story foundStory) {
        UserDTO foundUser = userService.getUserById(userId);

        if (foundUser != null) {
            InteractNotification notification = new InteractNotification();

            notification.setUserFullName(foundUser.getUserFullName());
            notification.setAvatarUrl(foundUser.getProfilePicture());
            notification.setContent("đã bày tỏ cảm xúc về tin của bạn.");
            notification.setRefId(foundStory.getId());
            notification.setTitle("Có người tương tác nè!");
            notification.setFromUserId(userId);
            notification.setToUserIds(List.of(foundStory.getUserId()));
            notification.setType(ENotificationType.valueOf(payload.getType().name()));
            notification.setCreateAt(new Date().getTime() + "");

            kafkaEventPublisher.publishInteractNoti(notification);
        }
    }

    private Reaction newReaction(Viewer viewer, String userId, DoReactRequest payload) {
        Reaction newReaction = new Reaction();

        newReaction.setType(payload.getType());
        newReaction.setViewer(viewer);
        newReaction.setCreatedAt(LocalDateTime.now());
        newReaction.setUserId(userId);

        return newReaction;
    }
}
