package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.PostTag;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.repository.SavePostRepository;
import vn.edu.tdtu.service.impl.UserServiceImpl;
import vn.edu.tdtu.service.intefaces.GroupService;
import vn.edu.tdtu.service.intefaces.InteractionService;
import vn.edu.tdtu.util.DateUtils;
import vn.edu.tdtu.util.SecurityContextUtils;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.enums.post.EPostType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostMapper {
    private final UserServiceImpl userService;
    private final GroupService groupService;
    private final InteractionService interactionService;
    private final SavePostRepository savePostRepository;
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final MediaResponseMapper mediaResponseMapper;

    public PostDTO mapToDto(String token, String postId, Post post, Map<String, Post> sharedPostsMap, boolean bulkFetch) {
        PostDTO postResponse = mapToDto(token, postId, post, bulkFetch);

        if (post.getType().equals(EPostType.SHARE)) {
            Post foundSharedPost = !bulkFetch ?
                    postRepository.findById(post.getSharedPostId()).orElse(null) :
                    sharedPostsMap.get(post.getSharedPostId());

            postResponse.setSharedPost(mapToBasePostDto(token, foundSharedPost, bulkFetch));
        }

        return postResponse;
    }

    public PostDTO mapToDto(String token, String postId, Post post, boolean bulkFetch) {

        PostDTO postResponse = mapToBasePostDto(token, post, bulkFetch);

        postResponse.setNoShared(0);
        postResponse.setSaved(savePostRepository.existsByUserIdAndPostIdsContains(SecurityContextUtils.getUserId(), postId));

        Map<EReactionType, List<ReactionDTO>> reactionsMap = interactionService.findReactionsByPostId(token, postId);

        int totalElements = reactionsMap != null ? reactionsMap.values().stream()
                .mapToInt(List::size)
                .sum() : 0;

        long commentsCount = interactionService.countCommentByPostId(token, postId);

        postResponse.setTopReacts(findTopReact(reactionsMap != null ? reactionsMap : new HashMap<>()));
        postResponse.setNoReactions(totalElements);
        postResponse.setNoComments(commentsCount);

        String userId;
        if (token != null && !token.isEmpty()) {
            userId = SecurityContextUtils.getUserId();

            postResponse.setMine(userId.equals(post.getUserId()));
        } else {
            userId = "";
        }

        if (reactionsMap != null && !userId.isEmpty()) {
            ReactionDTO react = findUserReaction(reactionsMap, userId);
            postResponse.setReacted(react != null ? react.getType() : null);
        }

        if (!bulkFetch && post.getType().equals(EPostType.SHARE)) {
            Post foundSharedPost = postRepository.findById(post.getSharedPostId()).orElse(null);

            postResponse.setSharedPost(mapToBasePostDto(token, foundSharedPost, bulkFetch));
        }

        return postResponse;
    }

    private PostDTO mapToBasePostDto(String token, Post post, boolean bulkFetch) {
        UserDTO postedUser = new UserDTO();

        if (!bulkFetch)
            postedUser = userService.findById(token, post.getUserId());

        List<UserDTO> taggedUsers = new ArrayList<>();

        if (post.getPostTags() != null)
            taggedUsers = post.getPostTags().stream().map(PostTag::getTaggedUser).toList();

        GroupDTO foundGroup = null;

        if (post.getGroupId() != null && !post.getGroupId().isEmpty())
            foundGroup = groupService.getGroupById(token, post.getGroupId());

        PostDTO postResponse = new PostDTO();

        postResponse.setId(post.getId());
        postResponse.setPrivacy(post.getPrivacy());
        postResponse.setContent(post.getContent());
        postResponse.setType(post.getType());
        postResponse.setMedias(mediaRepository.findAllById(post.getMediaIds() != null ? post.getMediaIds() : List.of())
                .stream()
                .map(mediaResponseMapper::mapToDTO)
                .toList());
        postResponse.setCreatedAt(DateUtils.localDateTimeToDate(post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now()));
        postResponse.setUpdatedAt(DateUtils.localDateTimeToDate(post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now()));
        postResponse.setGroupInfo(foundGroup);
        postResponse.setTaggedUsers(taggedUsers);
        postResponse.setBackground(post.getBackground());
        postResponse.setHiddenCreatedAt(DateUtils.localDateTimeToDate((post.getCreatedAt() != null ? post.getCreatedAt() : LocalDateTime.now())));
        postResponse.setUser(postedUser);

        return postResponse;
    }

    public static ReactionDTO findUserReaction(Map<EReactionType, List<ReactionDTO>> reactionsMap, String userId) {
        for (Map.Entry<EReactionType, List<ReactionDTO>> entry : reactionsMap.entrySet()) {
            List<ReactionDTO> reactsList = entry.getValue();

            for (ReactionDTO react : reactsList) {
                if (react.getUser().getId().equals(userId)) {
                    return react;
                }
            }
        }
        return null;
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
}
