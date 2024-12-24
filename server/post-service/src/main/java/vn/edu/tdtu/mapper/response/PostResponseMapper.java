package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.GroupInfo;
import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.dto.response.TopReacts;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.PostTag;
import vn.edu.tdtu.model.Reacts;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.PostShareRepository;
import vn.edu.tdtu.repository.SavePostRepository;
import vn.edu.tdtu.service.impl.UserServiceImpl;
import vn.edu.tdtu.service.intefaces.GroupService;
import vn.edu.tdtu.service.intefaces.InteractionService;
import vn.edu.tdtu.util.DateUtils;
import vn.edu.tdtu.util.JwtUtils;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostResponseMapper {
    private final UserServiceImpl userService;
    private final GroupService groupService;
    private final InteractionService interactionService;
    private final PostShareRepository postShareRepository;
    private final SavePostRepository savePostRepository;
    private final JwtUtils jwtUtils;

    public PostResponse mapToDto(String token, String postId, Post post, boolean bulkFetch){
        User postedUser = new User();
        if(!bulkFetch)
            postedUser =  userService.findById(token, post.getUserId());

        List<User> taggedUsers = post.getPostTags().stream().map(PostTag::getTaggedUser).toList();

        GroupInfo foundGroup = null;

        if(post.getGroupId() != null && !post.getGroupId().isEmpty())
            foundGroup = groupService.getGroupById(token, post.getGroupId());

        PostResponse postResponse = new PostResponse();

        postResponse.setId(post.getId());
        postResponse.setPrivacy(post.getPrivacy());
        postResponse.setContent(post.getContent());
        postResponse.setType(post.getType());
        postResponse.setImageUrls(post.getImageUrls());
        postResponse.setVideoUrls(post.getVideoUrls());
        postResponse.setCreatedAt(DateUtils.localDateTimeToDate(post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now()));
        postResponse.setUpdatedAt(DateUtils.localDateTimeToDate(post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now()));
        postResponse.setNoShared(postShareRepository.findBySharedPostId(post.getId()).size());
        postResponse.setGroupInfo(foundGroup);
        postResponse.setTaggedUsers(taggedUsers);
        postResponse.setHiddenCreatedAt(DateUtils.localDateTimeToDate((post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now())));
        postResponse.setUser(postedUser);
        postResponse.setSaved(savePostRepository.existsByUserIdAndPostIdsContains(SecurityContextUtils.getUserId(), postId));

        Map<EReactionType, List<Reacts>> reactionsMap = interactionService.findReactionsByPostId(token, postId);

        int totalElements = reactionsMap != null ? reactionsMap.values().stream()
                .mapToInt(List::size)
                .sum() : 0;

        long commentsCount = interactionService.countCommentByPostId(token, postId);

        postResponse.setTopReacts(findTopReact(reactionsMap != null ? reactionsMap : new HashMap<>()));
        postResponse.setNoReactions(totalElements);
        postResponse.setNoComments(commentsCount);

        String userId;
        if(token != null && !token.isEmpty()){
            userId = jwtUtils.getUserIdFromJwtToken(token);

            postResponse.setMine(userId.equals(post.getUserId()));
        }else{
            userId = "";
        }

        if(reactionsMap != null && !userId.isEmpty()){
            Reacts react = findUserReaction(reactionsMap, userId);
            postResponse.setReacted(react != null ? react.getType() : null);
        }

        return postResponse;
    }

    public static Reacts findUserReaction(Map<EReactionType, List<Reacts>> reactionsMap, String userId) {
        for (Map.Entry<EReactionType, List<Reacts>> entry : reactionsMap.entrySet()) {
            List<Reacts> reactsList = entry.getValue();

            for (Reacts react : reactsList) {
                if (react.getUser().getId().equals(userId)) {
                    return react;
                }
            }
        }
        return null;
    }

    private List<TopReacts> findTopReact(Map<EReactionType, List<Reacts>> reactionsMap){
        Map<EReactionType, List<Reacts>> sortedMap = reactionsMap.entrySet()
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
}
