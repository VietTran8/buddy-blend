package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.enums.EModerateType;
import vn.edu.tdtu.mapper.request.PostPostRequestMapper;
import vn.edu.tdtu.mapper.request.UpdatePostRequestMapper;
import vn.edu.tdtu.mapper.response.PostMapper;
import vn.edu.tdtu.message.ModerateMessage;
import vn.edu.tdtu.message.NewPostMessage;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.PostTag;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.CustomPostRepository;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.service.intefaces.GroupService;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.edu.tdtu.service.intefaces.UserService;
import vn.tdtu.common.dto.GroupDTO;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.notification.ENotificationType;
import vn.tdtu.common.enums.post.EFileType;
import vn.tdtu.common.enums.post.EPostType;
import vn.tdtu.common.enums.post.EPrivacy;
import vn.tdtu.common.enums.search.ESyncType;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.DateUtils;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.utils.StringUtils;
import vn.tdtu.common.viewmodel.PaginationResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostPostRequestMapper postPostRequestMapper;
    private final UpdatePostRequestMapper updatePostRequestMapper;
    private final CustomPostRepository customPostRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final MediaRepository mediaRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    private static Post getPostShare(SharePostRequest request, Post post, UserDTO foundUser) {
        Post postShare = new Post();

        postShare.setPrivacy(request.getPrivacy());
        postShare.setCreatedAt(LocalDateTime.now());
        postShare.setUserId(foundUser.getId());
        postShare.setDetached(false);
        postShare.setContent(request.getStatus());
        postShare.setSharedPostId(post.getId());
        postShare.setType(EPostType.SHARE);
        postShare.setNormalizedContent(StringUtils.toSlug(request.getStatus()));

        return postShare;
    }

    private static InteractNotification getInteractNotification(SharePostRequest request, Post post, UserDTO foundUser, String userId) {
        InteractNotification notification = new InteractNotification();

        notification.setAvatarUrl(foundUser.getProfilePicture());
        notification.setUserFullName(String.join(" ", foundUser.getFirstName(), foundUser.getMiddleName(), foundUser.getLastName()));
        notification.setContent(notification.getUserFullName() + " đã chia sẻ bài viết của bạn");
        notification.setRefId(request.getPostId());
        notification.setTitle("Có người tương tác nè!");
        notification.setFromUserId(userId);
        notification.setToUserIds(List.of(post.getUserId()));
        notification.setType(ENotificationType.SHARE);
        notification.setCreateAt(new Date());

        return notification;
    }

    @Override
    public Post findPostById(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public ResponseVM<?> findPostRespById(String token, String postId) {
        ResponseVM<PostDTO> response = new ResponseVM<>();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Post.POST_NOT_FOUND_ID, postId));

        response.setMessage(MessageCode.Post.POST_FETCHED);
        response.setData(postMapper.mapToDto(token, post.getId(), post, false));
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    @Override
    public ResponseVM<List<PostDTO>> findPostRespByIds(String token, FindByIdsReq req) {
        String userId = SecurityContextUtils.getUserId();

        ResponseVM<List<PostDTO>> response = new ResponseVM<>();
        List<String> ids = req.getIds();

        List<Post> foundPosts = postRepository.findByIdInAndDetachedNot(ids, true);

        List<String> sharedPostIds = foundPosts.stream().map(Post::getSharedPostId).toList();

        Map<String, Post> sharedPostMap = postRepository.findByIdInAndDetachedNot(sharedPostIds, true)
                .stream().collect(Collectors.toMap(
                        Post::getId,
                        post -> post
                ));

        List<String> postedUserIdsDistinct = Stream.concat(
                foundPosts.stream().map(Post::getUserId),
                foundPosts.stream().filter(post -> post.getType().equals(EPostType.SHARE))
                        .map(post -> sharedPostMap.get(post.getSharedPostId()).getUserId())
        ).distinct().toList();

        Map<String, UserDTO> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(UserDTO::getId, user -> user));

        List<PostDTO> posts = foundPosts.stream().map(
                post -> {
                    PostDTO postResponse = postMapper.mapToDto(token, post.getId(), post, true);
                    postResponse.setUser(postedUserMap.get(post.getUserId()));
                    postResponse.setMine(post.getUserId().equals(userId));

                    if (post.getType().equals(EPostType.SHARE))
                        postResponse.getSharedPost()
                                .setUser(postedUserMap.get(sharedPostMap.get(post.getSharedPostId()).getUserId()));

                    return postResponse;
                }
        ).toList();

        response.setCode(200);
        response.setData(posts);
        response.setMessage(MessageCode.Post.POST_FETCHED);

        return response;
    }

    @Override
    public ResponseVM<?> getUserIdByPostId(String postId) {
        ResponseVM<Map<String, String>> response = new ResponseVM<>();
        Map<String, String> data = new HashMap<>();

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post not found with id: " + postId));

        data.put("userId", post.getUserId());

        response.setMessage(MessageCode.User.USER_FETCHED);
        response.setData(data);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    @Override
    public ResponseVM<PostDTO> findDetachedPost(String token, String postId) {
        String authUserId = SecurityContextUtils.getUserId();

        Post foundPost = postRepository.findByIdAndDetachedAndUserId(postId, true, authUserId)
                .orElseThrow(() -> new BadRequestException(MessageCode.Post.POST_NOT_FOUND));

        ResponseVM<PostDTO> response = new ResponseVM<>();

        PostDTO responseData = postMapper.mapToDto(token, foundPost.getId(), foundPost, false);

        response.setMessage(MessageCode.Post.POST_FETCHED);
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    @Override
    public PostDTO mapToPostDTO(String token, Post post) {
        return postMapper.mapToDto(token, post.getId(), post, false);
    }

    @Override
    public ResponseVM<?> getGroupPosts(String token, String groupId, int page, int limit) {
        if (!groupService.allowFetchPost(token, groupId))
            throw new BadRequestException(MessageCode.Post.GROUP_NOT_PERMITTED);

        ResponseVM<PaginationResponseVM<PostDTO>> response = new ResponseVM<>();
        response.setMessage(MessageCode.Post.POST_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);

        Page<Post> groupPosts = postRepository.findByGroupIdAndDetachedNotOrderByCreatedAtDesc(groupId, true, PageRequest.of(page - 1, limit));
        List<String> postedUserIds = groupPosts.map(Post::getUserId).stream().distinct().toList();

        Map<String, UserDTO> postedUserMap = userService.findByIds(token, postedUserIds)
                .stream().collect(Collectors.toMap(
                        UserDTO::getId, user -> user
                ));

        String userId = SecurityContextUtils.getUserId();

        response.setData(new PaginationResponseVM<>(
                page,
                limit,
                groupPosts.getTotalPages(),
                groupPosts.stream()
                        .map(
                                post -> {
                                    PostDTO postResponse = postMapper.mapToDto(token, post.getId(), post, true);
                                    postResponse.setMine(post.getUserId().equals(userId));
                                    postResponse.setUser(postedUserMap.get(post.getUserId()));

                                    return postResponse;
                                }
                        ).toList()
        ));

        return response;
    }

    @Override
//    @Cacheable(key = "T(java.util.Objects).hash(#a1, #a2, #a3)", value = "news-feed", unless = "#result.data['posts'].isEmpty() or #result.data['posts'] == null")
    public ResponseVM<?> getNewsFeed(String token, int page, int size, String startTime) {
        FetchNewsFeedReq req = new FetchNewsFeedReq();
        req.setPage(page);
        req.setSize(size);
        req.setStartTime(DateUtils.stringToLocalDate(startTime));

        ResponseVM<PaginationResponseVM<PostDTO>> response = new ResponseVM<>();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(UserDTO::getId)
                .toList();

        List<String> userGroupIds = groupService.getMyGroups(token)
                .stream()
                .map(GroupDTO::getId)
                .toList();

        String authUserId = SecurityContextUtils.getUserId();

        log.info("[getNewsFeed] - authUserId: {}", authUserId);

        Page<Post> fetchedPosts = customPostRepository.findNewsFeed(authUserId, friendIds, userGroupIds, req.getStartTime(), page, size);

        List<String> sharedPostIds = fetchedPosts.map(Post::getSharedPostId).stream().toList();

        Map<String, Post> sharedPostMap = postRepository.findByIdInAndDetachedNot(sharedPostIds, true)
                .stream().collect(Collectors.toMap(
                        Post::getId,
                        Function.identity()
                ));

        List<String> postedUserIdsDistinct = Stream.concat(
                fetchedPosts.stream().map(Post::getUserId),
                fetchedPosts.stream().filter(post -> post.getType().equals(EPostType.SHARE))
                        .map(post -> sharedPostMap.get(post.getSharedPostId()).getUserId())
        ).distinct().toList();

        Map<String, UserDTO> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        List<PostDTO> posts = fetchedPosts.stream().parallel().map(
                post -> {
                    PostDTO postResponse = postMapper.mapToDto(token, post.getId(), post, sharedPostMap, true);
                    postResponse.setUser(postedUserMap.get(post.getUserId()));
                    postResponse.setMine(post.getUserId().equals(authUserId));

                    if (post.getType().equals(EPostType.SHARE))
                        postResponse.getSharedPost()
                                .setUser(postedUserMap.get(sharedPostMap.get(post.getSharedPostId()).getUserId()));

                    return postResponse;
                }
        ).filter(post -> {
            if (!post.getType().equals(EPostType.GROUP))
                return true;

            if (post.getGroupInfo() == null)
                return false;

            if (post.getGroupInfo().isPrivate())
                return userGroupIds.contains(post.getGroupInfo().getId());

            return true;
        }).peek(post -> doPostCensoring(token, post, friendIds)).toList();

        PaginationResponseVM<PostDTO> paginationResponse = new PaginationResponseVM<>();

        paginationResponse.setData(posts);
        paginationResponse.setPage(page);
        paginationResponse.setLimit(size);
        paginationResponse.setTotalPages(fetchedPosts.getTotalPages());

        response.setData(paginationResponse);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Post.POST_FETCHED);

        return response;
    }

    private void doPostCensoring(String token, PostDTO postResponse, List<String> friendIds) {
        String authUserId = SecurityContextUtils.getUserId();

        PostDTO postShare = postResponse.getSharedPost();

        if (postShare == null)
            return;

        if (
                (postShare.getPrivacy().equals(EPrivacy.PRIVATE) && !postShare.getUser().getId().equals(authUserId)) ||
                        (postShare.getPrivacy().equals(EPrivacy.ONLY_FRIENDS) && (!friendIds.contains(postShare.getUser().getId()) && !postShare.getUser().getId().equals(authUserId))) ||
                        (postShare.getType().equals(EPostType.GROUP) && !groupService.allowFetchPost(token, postShare.getGroupInfo().getId()))
        ) {
            hideIllegalPostDTO(postResponse);
        }
    }

    @Override
    public ResponseVM<?> findByContentContaining(String token, String key) {
        ResponseVM<List<PostDTO>> response = new ResponseVM<>();
        response.setData(postRepository.findByContent(key).stream().map(
                p -> postMapper.mapToDto(token, p.getId(), p, false)
        ).toList());
        response.setMessage(MessageCode.Post.POST_FETCHED);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    @Override
    public ResponseVM<?> savePost(String token, CreatePostRequest postRequest) {
        Post post = postPostRequestMapper.mapToObject(postRequest);

        List<UserDTO> taggedUser = userService
                .findByIds(token, postRequest.getPostTags()
                        .stream()
                        .map(PostTagReqDTO::getTaggedUserId)
                        .toList()
                );

        post.setPostTags(
                taggedUser.stream().map(
                        user -> {
                            PostTag postTag = new PostTag();
                            postTag.setId(UUID.randomUUID().toString());
                            postTag.setCreatedAt(LocalDateTime.now());
                            postTag.setTaggedUser(user);
                            return postTag;
                        }
                ).toList()
        );

        post.setUserId(SecurityContextUtils.getUserId());
        post = postRepository.save(post);

        PostDTO postResponse = postMapper.mapToDto(token, post.getId(), post, false);
        postResponse.setMine(post.getUserId().equals(SecurityContextUtils.getUserId()));

        ResponseVM<PostDTO> response = new ResponseVM<>();
        response.setMessage(MessageCode.Post.POST_SAVED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(postResponse);

        ModerateMessage message = new ModerateMessage();
        message.setImageUrls(postRequest.getMedias()
                .stream()
                .filter(media -> EFileType.TYPE_IMG.equals(media.getType()))
                .map(Media::getUrl)
                .toList());
        message.setRefId(post.getId());
        message.setType(EModerateType.TYPE_POST);
        message.setContent(postRequest.getContent());

        log.info(message.toString());
        kafkaEventPublisher.pubModerateMessage(message);
        kafkaEventPublisher.pubSyncPostMessage(post, ESyncType.TYPE_CREATE);

        handleSendNewPostNotification(token, postResponse);

        return response;
    }

    @Override
    public ResponseVM<?> updatePostContent(String token, UpdatePostContentRequest request) {
        String userId = SecurityContextUtils.getUserId();

        Post foundPost = findPostById(request.getId());
        ResponseVM<PostDTO> response = new ResponseVM<>();

        if (foundPost != null) {
            if (!foundPost.getUserId().equals(userId))
                throw new BadRequestException(MessageCode.Post.POST_CAN_NOT_UPDATE_OTHERS);

            updatePostRequestMapper.bindToObject(request, foundPost);

            List<UserDTO> taggedUser = userService.findByIds(token, request.getTaggingUsers());

            foundPost.setPostTags(
                    taggedUser.stream().map(
                            user -> {
                                PostTag postTag = new PostTag();
                                postTag.setId(UUID.randomUUID().toString());
                                postTag.setCreatedAt(LocalDateTime.now());
                                postTag.setTaggedUser(user);
                                return postTag;
                            }
                    ).toList()
            );

            postRepository.save(foundPost);

            response.setMessage(MessageCode.Post.POST_UPDATED);
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(postMapper.mapToDto(token, foundPost.getId(), foundPost, false));

            kafkaEventPublisher.pubSyncPostMessage(foundPost, ESyncType.TYPE_UPDATE);

            return response;
        }

        return response;
    }

    @Override
    public ResponseVM<?> deletePost(String postId) {
        String userId = SecurityContextUtils.getUserId();

        Post foundPost = findPostById(postId);

        ResponseVM<Map<String, String>> response = new ResponseVM<>();
        Map<String, String> data = new HashMap<>();

        if (foundPost != null) {
            if (foundPost.getUserId().equals(userId)) {
                mediaRepository.deleteByIdIn(foundPost.getMediaIds());
                postRepository.delete(foundPost);

                data.put("deletedId", foundPost.getId());

                response.setMessage(MessageCode.Post.POST_UPDATED);
                response.setCode(HttpServletResponse.SC_OK);
                response.setData(data);

                kafkaEventPublisher.pubSyncPostMessage(foundPost, ESyncType.TYPE_DELETE);

                return response;

            }

            response.setMessage(MessageCode.Post.POST_CAN_NOT_DELETE_OTHERS);
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);

            return response;
        }

        return response;
    }

    @Override
    public ResponseVM<?> sharePost(String token, SharePostRequest request) {
        ResponseVM<PostDTO> response = new ResponseVM<>();

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new BadRequestException(MessageCode.Post.POST_NOT_FOUND_ID, request.getPostId()));

        if (post.getType().equals(EPostType.GROUP)) {
            GroupDTO foundGroupInfo = groupService.getGroupById(token, post.getGroupId());

            if (foundGroupInfo.isPrivate())
                throw new BadRequestException(MessageCode.Post.GROUP_NOT_PERMITTED);
        }

        String userId = SecurityContextUtils.getUserId();
        UserDTO foundUser = userService.findById(token, userId);

        if (foundUser == null) {
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND);
        }

        Post postShare = getPostShare(request, post, foundUser);
        postRepository.save(postShare);

        if (!post.getUserId().equals(userId)) {
            InteractNotification notification = getInteractNotification(request, post, foundUser, userId);
            kafkaEventPublisher.pubSharePostMessage(notification);
        }

        response.setMessage(MessageCode.Post.POST_SHARED);
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(postMapper.mapToDto(token, post.getId(), post, false));

        return response;
    }

    @Override
    public ResponseVM<PaginationResponseVM<PostDTO>> findUserPosts(String token, String uId, int page, int size) {
        String userId = SecurityContextUtils.getUserId();

        if (uId.isEmpty())
            return findPostsByUserId(token, userId, page, size);

        return findPostsByUserId(token, uId, page, size);
    }

    private void hideIllegalPostDTO(PostDTO postResponse) {
        postResponse.setSharedPost(null);
    }

    private void handleSendNewPostNotification(String tokenHeader, PostDTO postResponse) {
        NewPostMessage newPostMessage = new NewPostMessage();
        newPostMessage.setPost(postResponse);

        if ((EPostType.NORMAL.equals(postResponse.getType()) || EPostType.SHARE.equals(postResponse.getType())) &&
                !EPrivacy.PRIVATE.equals(postResponse.getPrivacy())
        ) {
            List<String> postedUserFriendIds = userService.findUserFriendIdsByUserId(tokenHeader, postResponse.getUser().getId());
            newPostMessage.setBroadcastIds(postedUserFriendIds);

            kafkaEventPublisher.pubNewPostMessage(newPostMessage);
        } else if (EPostType.GROUP.equals(postResponse.getType())) {
            List<String> groupUserIds = groupService.getMemberIdList(tokenHeader, postResponse.getGroupInfo().getId());
            newPostMessage.setBroadcastIds(groupUserIds);

            kafkaEventPublisher.pubNewPostMessage(newPostMessage);
        }
    }

    private ResponseVM<PaginationResponseVM<PostDTO>> findPostsByUserId(String token, String userId, int page, int size) {
        String authUserId = SecurityContextUtils.getUserId();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(UserDTO::getId)
                .toList();

        Page<Post> fetchedPosts = postRepository.findByUserIdOrPostTagsTaggedUserIdWithPrivacy(
                userId,
                userId,
                friendIds,
                SecurityContextUtils.getUserId(),
                PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")))
        );

        List<String> sharedPostIds = fetchedPosts.map(Post::getSharedPostId).stream().toList();

        Map<String, Post> sharedPostMap = postRepository.findByIdInAndDetachedNot(sharedPostIds, true)
                .stream().collect(Collectors.toMap(
                        Post::getId,
                        post -> post
                ));

        List<String> postedUserIdsDistinct = Stream.concat(
                fetchedPosts.stream().map(Post::getUserId),
                fetchedPosts.stream().filter(post -> post.getType().equals(EPostType.SHARE))
                        .map(post -> sharedPostMap.get(post.getSharedPostId()).getUserId())
        ).distinct().toList();

        Map<String, UserDTO> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        List<PostDTO> myPosts = fetchedPosts.stream().map(post -> {
            PostDTO postResponse = postMapper.mapToDto(token, post.getId(), post, sharedPostMap, true);
            postResponse.setMine(post.getUserId().equals(authUserId));
            postResponse.setUser(postedUserMap.get(post.getUserId()));

            if (post.getType().equals(EPostType.SHARE))
                postResponse.getSharedPost()
                        .setUser(postedUserMap.get(sharedPostMap.get(post.getSharedPostId()).getUserId()));

            return postResponse;

        }).peek(post -> doPostCensoring(token, post, friendIds)).toList();

        ResponseVM<PaginationResponseVM<PostDTO>> response = new ResponseVM<>();

        PaginationResponseVM<PostDTO> paginationResponse = new PaginationResponseVM<>();

        paginationResponse.setData(myPosts);
        paginationResponse.setPage(page);
        paginationResponse.setLimit(size);
        paginationResponse.setTotalPages(fetchedPosts.getTotalPages());

        response.setData(paginationResponse);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Post.POST_FETCHED);

        return response;
    }
}