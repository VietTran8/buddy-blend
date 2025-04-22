package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.dto.response.ReactResponse;
import vn.edu.tdtu.dto.response.TopReacts;
import vn.edu.tdtu.enums.ENotificationType;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.DoReactMapper;
import vn.edu.tdtu.mapper.ReactResponseMapper;
import vn.edu.tdtu.model.Reactions;
import vn.edu.tdtu.model.data.Post;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.ReactionRepository;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.edu.tdtu.service.interfaces.ReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;

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
    public ResDTO<?> doReaction(String token, DoReactRequest request) {
        ResDTO<List<TopReacts>> response = new ResDTO<>();
        String userId = SecurityContextUtils.getUserId();
        String postId = request.getPostId();

        Post foundPost = postService.findById(token, request.getPostId());

        if (foundPost == null)
            throw new BadRequestException(MessageCode.POST_NOT_FOUND_ID, request.getPostId());

        AtomicReference<Boolean> isCreateNew = new AtomicReference<>();
        isCreateNew.set(false);

        Reactions reaction = reactMapper.mapToObject(userId, request);
        reaction.setPostId(postId);

        reactionRepository.findByUserIdAndPostId(reaction.getUserId(), postId)
                .ifPresentOrElse(
                        r -> {
                            if (r.getType().equals(reaction.getType())) {
                                reactionRepository.delete(r);
                                response.setMessage(MessageCode.REACTION_UNCREATED);
                            } else {
                                r.setType(reaction.getType());
                                r.setCreatedAt(LocalDateTime.now());
                                reactionRepository.save(r);

                                response.setMessage(MessageCode.REACTION_UPDATED);
                                isCreateNew.set(true);
                            }
                        }, () -> {
                            reactionRepository.save(reaction);
                            response.setMessage(MessageCode.REACTION_CREATED);
                            isCreateNew.set(true);
                        }
                );

        if (isCreateNew.get()) {
            User foundUser = userService.findById(token, userId);
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

        Map<EReactionType, List<ReactResponse>> newPostReacts = getReactsByPostId(token, reaction.getPostId()).getData();
        List<TopReacts> topReacts = findTopReact(newPostReacts != null ? newPostReacts : new HashMap<>());

        response.setData(topReacts);
        response.setCode(200);

        return response;
    }

    private List<TopReacts> findTopReact(Map<EReactionType, List<ReactResponse>> reactionsMap) {
        Map<EReactionType, List<ReactResponse>> sortedMap = reactionsMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()))
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        List<TopReacts> topReacts = new ArrayList<>();

        sortedMap.forEach((k, v) -> {
            TopReacts react = new TopReacts();
            react.setCount(v.size());
            react.setType(k);

            topReacts.add(react);
        });

        return topReacts;
    }

    @Override
    public ResDTO<Map<EReactionType, List<ReactResponse>>> getReactsByPostId(String token, String postId) {
        ResDTO<Map<EReactionType, List<ReactResponse>>> response = new ResDTO<>();
        String userId = SecurityContextUtils.getUserId();

        List<Reactions> reactions = reactionRepository.findReactionsByPostIdOrderByCreatedAtDesc(postId);
        List<String> userIds = reactions.stream().map(Reactions::getUserId).toList();
        List<User> users = userService.findByIds(token, userIds);

        Map<EReactionType, List<ReactResponse>> reactResponses = reactions
                .stream()
                .map(r -> reactResponseMapper.mapToDto(userId, r, users))
                .collect(Collectors.groupingBy(ReactResponse::getType));

        response.setCode(200);
        response.setData(reactResponses);
        response.setMessage(MessageCode.REACTION_FETCHED);

        return response;
    }
}
