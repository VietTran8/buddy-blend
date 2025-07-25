package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.mapper.DoReactMapper;
import vn.edu.tdtu.mapper.ReactResponseMapper;
import vn.edu.tdtu.model.Reactions;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.ReactionRepository;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.enums.notification.ENotificationType;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final DoReactMapper reactMapper;
    private final ReactResponseMapper reactResponseMapper;
    private final UserService userService;
    private final KafkaEventPublisher kafkaMsgService;
    private final PostService postService;

    @Override
    public ResponseVM<?> doReaction(DoReactRequest request) {
        ResponseVM<List<ReactionDTO>> response = new ResponseVM<>();
        String userId = SecurityContextUtils.getUserId();
        String postId = request.getPostId();

        PostDTO foundPost = postService.findById(request.getPostId());

        if (foundPost == null)
            throw new BadRequestException(MessageCode.Post.POST_NOT_FOUND_ID, request.getPostId());

        AtomicReference<Boolean> isCreateNew = new AtomicReference<>();
        isCreateNew.set(false);

        Reactions reaction = reactMapper.mapToObject(userId, request);
        reaction.setPostId(postId);

        reactionRepository.findByUserIdAndPostId(reaction.getUserId(), postId)
                .ifPresentOrElse(
                        r -> {
                            if (r.getType().equals(reaction.getType())) {
                                reactionRepository.delete(r);
                                response.setMessage(MessageCode.Interaction.REACTION_UNCREATED);
                            } else {
                                r.setType(reaction.getType());
                                r.setCreatedAt(LocalDateTime.now());
                                reactionRepository.save(r);

                                response.setMessage(MessageCode.Interaction.REACTION_UPDATED);
                                isCreateNew.set(true);
                            }
                        }, () -> {
                            reactionRepository.save(reaction);
                            response.setMessage(MessageCode.Interaction.REACTION_CREATED);
                            isCreateNew.set(true);
                        }
                );

        if (isCreateNew.get()) {
            UserDTO foundUser = userService.findById(userId);
            if (foundUser != null && !foundUser.getId().equals(foundPost.getUser().getId())) {
                InteractNotification notification = new InteractNotification();
                notification.setUserFullName(String.join(" ", foundUser.getFirstName(), foundUser.getMiddleName(), foundUser.getLastName()));
                notification.setAvatarUrl(foundUser.getProfilePicture());
                notification.setContent("đã bày tỏ cảm xúc về bài viết của bạn.");
                notification.setRefId(reaction.getPostId());
                notification.setTitle("Có người tương tác nè!");
                notification.setFromUserId(userId);
                notification.setToUserIds(List.of(foundPost.getUser().getId()));
                notification.setType(ENotificationType.valueOf(reaction.getType().name()));
                notification.setCreateAt(new Date().getTime() + "");

                kafkaMsgService.publishInteractNoti(notification);
            }
        }

        Map<EReactionType, List<ReactionDTO>> newPostReacts = getReactsByPostId(reaction.getPostId()).getData();
        List<ReactionDTO> topReacts = findTopReact(newPostReacts != null ? newPostReacts : new HashMap<>());

        response.setData(topReacts);
        response.setCode(200);

        return response;
    }

    private List<ReactionDTO> findTopReact(Map<EReactionType, List<ReactionDTO>> reactionsMap) {
        Map<EReactionType, List<ReactionDTO>> sortedMap = reactionsMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()))
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        List<ReactionDTO> topReacts = new ArrayList<>();

        sortedMap.forEach((k, v) -> {
            ReactionDTO react = new ReactionDTO();
            react.setCount(v.size());
            react.setType(k);

            topReacts.add(react);
        });

        return topReacts;
    }

    @Override
    public ResponseVM<Map<EReactionType, List<ReactionDTO>>> getReactsByPostId(String postId) {
        ResponseVM<Map<EReactionType, List<ReactionDTO>>> response = new ResponseVM<>();
        String userId = SecurityContextUtils.getUserId();

        List<Reactions> reactions = reactionRepository.findReactionsByPostIdOrderByCreatedAtDesc(postId);
        List<String> userIds = reactions.stream().map(Reactions::getUserId).toList();
        List<UserDTO> users = userService.findByIds(userIds);

        Map<EReactionType, List<ReactionDTO>> reactResponses = reactions
                .stream()
                .map(r -> reactResponseMapper.mapToDto(userId, r, users))
                .collect(Collectors.groupingBy(ReactionDTO::getType));

        response.setCode(200);
        response.setData(reactResponses);
        response.setMessage(MessageCode.Interaction.REACTION_FETCHED);

        return response;
    }
}
