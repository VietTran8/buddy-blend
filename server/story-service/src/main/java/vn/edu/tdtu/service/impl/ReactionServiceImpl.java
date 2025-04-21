package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.Message;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.DoReactRequest;
import vn.edu.tdtu.dto.response.DoReactResponse;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.enums.ENotificationType;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.Reaction;
import vn.edu.tdtu.model.Story;
import vn.edu.tdtu.model.Viewer;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.StoryRepository;
import vn.edu.tdtu.repository.ViewerRepository;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;

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
    public ResDTO<DoReactResponse> doReact(String accessToken, DoReactRequest payload){
        String userId = SecurityContextUtils.getUserId();

        Story foundStory = storyRepository.findById(payload.getStoryId())
                .orElseThrow(() -> new BadRequestException(Message.STORY_NOT_FOUND_MSG));

        if(foundStory.getUserId().equals(userId))
            throw new BadRequestException(Message.STORY_CAN_NOT_SELF_REACT_MSG);

        Viewer foundViewer = viewerRepository.findTopByStoryAndUserId(foundStory, userId)
                .orElseThrow(() -> new BadRequestException(Message.VIEWER_NOT_FOUND_MSG));

        foundViewer.getReactions().add(newReaction(foundViewer, userId, payload));

        viewerRepository.save(foundViewer);

        sendNotification(accessToken, payload, userId, foundStory);

        return new ResDTO<>(
                Message.REACTION_CREATED_MSG,
                new DoReactResponse(payload),
                HttpServletResponse.SC_CREATED
        );
    }

    private void sendNotification(String accessToken, DoReactRequest payload, String userId, Story foundStory) {
        User foundUser = userService.getUserById(accessToken, userId);

        if(foundUser != null) {
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
