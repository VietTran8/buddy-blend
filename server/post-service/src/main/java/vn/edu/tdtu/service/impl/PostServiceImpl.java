package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.GroupInfo;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.dto.response.PaginationResponse;
import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.enums.*;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.request.PostPostRequestMapper;
import vn.edu.tdtu.mapper.request.UpdatePostRequestMapper;
import vn.edu.tdtu.mapper.response.PostResponseMapper;
import vn.edu.tdtu.message.ModerateMessage;
import vn.edu.tdtu.message.NewPostMessage;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.PostTag;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.BannedWordRepository;
import vn.edu.tdtu.repository.CustomPostRepository;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.service.intefaces.GroupService;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.edu.tdtu.service.intefaces.UserService;
import vn.edu.tdtu.util.DateUtils;
import vn.edu.tdtu.util.SecurityContextUtils;
import vn.edu.tdtu.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostResponseMapper postResponseMapper;
    private final PostPostRequestMapper postPostRequestMapper;
    private final UpdatePostRequestMapper updatePostRequestMapper;
    private final CustomPostRepository customPostRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final MediaRepository mediaRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public Post findPostById(String postId){
        return postRepository.findById(postId).orElse(null);
    }

    public ResDTO<?> findPostRespById(String token, String postId){
        ResDTO<PostResponse> response = new ResDTO<>();

        Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new BadRequestException("Post not found with id: " + postId));

        response.setMessage("success");
        response.setData(postResponseMapper.mapToDto(token, post.getId(), post, false));
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    public ResDTO<List<PostResponse>> findPostRespByIds(String token, FindByIdsReq req){
        String userId = SecurityContextUtils.getUserId();

        ResDTO<List<PostResponse>> response = new ResDTO<>();
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

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<PostResponse> posts = foundPosts.stream().map(
                post -> {
                    PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, true);
                    postResponse.setUser(postedUserMap.get(post.getUserId()));
                    postResponse.setMine(post.getUserId().equals(userId));

                    if(post.getType().equals(EPostType.SHARE))
                        postResponse.getSharedPost()
                                .setUser(postedUserMap.get(sharedPostMap.get(post.getSharedPostId()).getUserId()));

                    return postResponse;
                }
        ).toList();

        response.setCode(200);
        response.setData(posts);
        response.setMessage("success");

        return response;
    }

    public ResDTO<?> getUserIdByPostId(String postId){
        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post not found with id: " + postId));

        data.put("userId", post.getUserId());

        response.setMessage("success");
        response.setData(data);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    public ResDTO<PostResponse> findDetachedPost(String token, String postId) {
        String authUserId = SecurityContextUtils.getUserId();

        Post foundPost = postRepository.findByIdAndDetachedAndUserId(postId, true, authUserId)
                .orElseThrow(() -> new BadRequestException("Post not found!"));

        ResDTO<PostResponse> response = new ResDTO<>();

        PostResponse responseData = postResponseMapper.mapToDto(token, foundPost.getId(), foundPost, false);

        response.setMessage("Post fetched successfully");
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    public PostResponse mapToPostResponse (String token, Post post) {
        return postResponseMapper.mapToDto(token, post.getId(), post, false);
    }

    public ResDTO<?> getGroupPosts(String token, String groupId, int page, int limit) {
        if(!groupService.allowFetchPost(token, groupId))
            throw new BadRequestException("This group is private and you are not joined it");

        ResDTO<PaginationResponse<PostResponse>> response = new ResDTO<>();
        response.setMessage("Group posts fetched successfully");
        response.setCode(HttpServletResponse.SC_OK);

        Page<Post> groupPosts = postRepository.findByGroupIdAndDetachedNotOrderByCreatedAtDesc(groupId, true, PageRequest.of(page - 1, limit));
        List<String> postedUserIds = groupPosts.map(Post::getUserId).stream().distinct().toList();

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIds)
                .stream().collect(Collectors.toMap(
                        User::getId, user -> user
                ));

        String userId = SecurityContextUtils.getUserId();

        response.setData(new PaginationResponse<>(
                page,
                limit,
                groupPosts.getTotalPages(),
                groupPosts.stream()
                        .map(
                        post -> {
                            PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, true);
                            postResponse.setMine(post.getUserId().equals(userId));
                            postResponse.setUser(postedUserMap.get(post.getUserId()));

                            return postResponse;
                        }
                ).toList()
        ));

        return response;
    }

//    @Cacheable(key = "T(java.util.Objects).hash(#a1, #a2, #a3)", value = "news-feed", unless = "#result.data['posts'].isEmpty() or #result.data['posts'] == null")
    public ResDTO<?> getNewsFeed(String token, int page, int size, String startTime) {
        FetchNewsFeedReq req = new FetchNewsFeedReq();
        req.setPage(page);
        req.setSize(size);
        req.setStartTime(DateUtils.stringToLocalDate(startTime));

        ResDTO<PaginationResponse<PostResponse>> response = new ResDTO<>();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(User::getId)
                .toList();

        List<String> groupIds = groupService.getMyGroups(token)
                .stream()
                .map(GroupInfo::getId)
                .toList();

        String authUserId = SecurityContextUtils.getUserId();

        Page<Post> fetchedPosts = customPostRepository.findNewsFeed(authUserId, friendIds, groupIds, req.getStartTime(), page, size);

        List<String> sharedPostIds = fetchedPosts.map(Post::getSharedPostId).stream().toList();

        Map<String, Post> sharedPostMap = postRepository.findByIdInAndDetachedNot(sharedPostIds, true)
                .stream().collect(Collectors.toMap(
                        Post::getId,
                        post -> post
                ));

        //New codes
        List<String> postedUserIdsDistinct = Stream.concat(
                fetchedPosts.stream().map(Post::getUserId),
                fetchedPosts.stream().filter(post -> post.getType().equals(EPostType.SHARE))
                        .map(post -> sharedPostMap.get(post.getSharedPostId()).getUserId())
        ).distinct().toList();

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        //End new codes

        List<PostResponse> posts = fetchedPosts.stream().map(
                post -> {
                    PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, sharedPostMap, true);
                    postResponse.setUser(postedUserMap.get(post.getUserId()));
                    postResponse.setMine(post.getUserId().equals(authUserId));

                    if(post.getType().equals(EPostType.SHARE))
                        postResponse.getSharedPost()
                                .setUser(postedUserMap.get(sharedPostMap.get(post.getSharedPostId()).getUserId()));

                    return postResponse;
                }
        ).filter(post -> {
            if(!post.getType().equals(EPostType.GROUP)) {
                return true;
            }

            if(post.getGroupInfo().isPrivate())
                return groupIds.contains(post.getGroupInfo().getId());

            return true;
        }).peek(post -> doPostCensoring(token, post, friendIds)).toList();

        PaginationResponse<PostResponse> paginationResponse = new PaginationResponse<>();

        paginationResponse.setData(posts);
        paginationResponse.setPage(page);
        paginationResponse.setLimit(size);
        paginationResponse.setTotalPages(fetchedPosts.getTotalPages());

        response.setData(paginationResponse);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");

        return response;
    }

    private void doPostCensoring(String token, PostResponse postResponse, List<String> friendIds) {
        String authUserId = SecurityContextUtils.getUserId();

        PostResponse postShare = postResponse.getSharedPost();

        if(postShare == null)
            return;

        if(
                (postShare.getPrivacy().equals(EPrivacy.PRIVATE) && !postShare.getUser().getId().equals(authUserId)) ||
                        (postShare.getPrivacy().equals(EPrivacy.ONLY_FRIENDS) && (!friendIds.contains(postShare.getUser().getId()) && !postShare.getUser().getId().equals(authUserId))) ||
                        (postShare.getType().equals(EPostType.GROUP) && !groupService.allowFetchPost(token, postShare.getGroupInfo().getId()))
        ){
            hideIllegalPostResponse(postResponse);
        }
    }

    public ResDTO<?> findByContentContaining(String token, String key){
        ResDTO<List<PostResponse>> response = new ResDTO<>();
        response.setData(postRepository.findByContent(key).stream().map(
                p -> postResponseMapper.mapToDto(token, p.getId(), p, false)
        ).toList());
        response.setMessage("success");
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    public ResDTO<?> savePost(String token, CreatePostRequest postRequest){
        Post post = postPostRequestMapper.mapToObject(postRequest);

        List<User> taggedUser = userService
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

        PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, false);
        postResponse.setMine(post.getUserId().equals(SecurityContextUtils.getUserId()));

        ResDTO<PostResponse> response = new ResDTO<>();
        response.setMessage("Đã đăng bài viết");
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

    public ResDTO<?> updatePostContent(String token, UpdatePostContentRequest request){
        String userId = SecurityContextUtils.getUserId();

        Post foundPost = findPostById(request.getId());
        ResDTO<PostResponse> response = new ResDTO<>();

        if(foundPost != null){
            if(!foundPost.getUserId().equals(userId))
                throw new BadRequestException("Không thể cập nhật bài viết của người khác");

            updatePostRequestMapper.bindToObject(request, foundPost);

            List<User> taggedUser = userService.findByIds(token, request.getTaggingUsers());

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

            response.setMessage("Đã cập nhật bài viết");
            response.setCode(HttpServletResponse.SC_OK);
            response.setData(postResponseMapper.mapToDto(token, foundPost.getId(), foundPost, false));

            kafkaEventPublisher.pubSyncPostMessage(foundPost, ESyncType.TYPE_UPDATE);

            return response;
        }

        return response;
    }

    public ResDTO<?> deletePost(String postId){
        String userId = SecurityContextUtils.getUserId();

        Post foundPost = findPostById(postId);

        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();

        if(foundPost != null){
            if(foundPost.getUserId().equals(userId)){
                mediaRepository.deleteByIdIn(foundPost.getMediaIds());
                postRepository.delete(foundPost);

                data.put("deletedId", foundPost.getId());

                response.setMessage("Đã xoá bài viết");
                response.setCode(HttpServletResponse.SC_OK);
                response.setData(data);

                kafkaEventPublisher.pubSyncPostMessage(foundPost, ESyncType.TYPE_DELETE);

                return response;

            }

            response.setMessage("Không thể xóa bài viết của người khác");
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);

            return response;
        }

        return response;
    }

    public ResDTO<?> sharePost(String token, SharePostRequest request){
        ResDTO<PostResponse> response = new ResDTO<>();

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy bài viết"));

        if(post.getType().equals(EPostType.GROUP)) {
            GroupInfo foundGroupInfo = groupService.getGroupById(token, post.getGroupId());

            if(foundGroupInfo.isPrivate())
                throw new BadRequestException("Không thể chia sẻ bài viết trong nhóm riêng tư");
        }

        String userId = SecurityContextUtils.getUserId();
        User foundUser = userService.findById(token, userId);

        if(foundUser == null) {
            throw new BadRequestException("Không tìm thấy người dùng");
        }

        Post postShare = getPostShare(request, post, foundUser);
        postRepository.save(postShare);

        if(!post.getUserId().equals(userId)){
            InteractNotification notification = getInteractNotification(request, post, foundUser, userId);
            kafkaEventPublisher.pubSharePostMessage(notification);
        }

        response.setMessage("Đã chia sẻ bài viết");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(postResponseMapper.mapToDto(token, post.getId(), post, false));

        return response;
    }

    public ResDTO<PaginationResponse<PostResponse>> findUserPosts(String token, String uId, int page, int size) {
        String userId = SecurityContextUtils.getUserId();

        if (uId.isEmpty())
            return findPostsByUserId(token, userId, page, size);

        return findPostsByUserId(token, uId, page, size);
    }

    private void hideIllegalPostResponse(PostResponse postResponse) {
        postResponse.setSharedPost(null);
    }

    private static Post getPostShare(SharePostRequest request, Post post, User foundUser) {
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

    private static InteractNotification getInteractNotification(SharePostRequest request, Post post, User foundUser, String userId) {
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

    private void handleSendNewPostNotification(String tokenHeader, PostResponse postResponse){
        NewPostMessage newPostMessage = new NewPostMessage();
        newPostMessage.setPost(postResponse);

        if((EPostType.NORMAL.equals(postResponse.getType()) || EPostType.SHARE.equals(postResponse.getType())) &&
                !EPrivacy.PRIVATE.equals(postResponse.getPrivacy())
        ) {
            List<String> postedUserFriendIds = userService.findUserFriendIdsByUserId(tokenHeader, postResponse.getUser().getId());
            newPostMessage.setBroadcastIds(postedUserFriendIds);

            kafkaEventPublisher.pubNewPostMessage(newPostMessage);
        } else if(EPostType.GROUP.equals(postResponse.getType())) {
            List<String> groupUserIds = groupService.getMemberIdList(tokenHeader, postResponse.getGroupInfo().getId());
            newPostMessage.setBroadcastIds(groupUserIds);

            kafkaEventPublisher.pubNewPostMessage(newPostMessage);
        }
    }

    private ResDTO<PaginationResponse<PostResponse>> findPostsByUserId(String token, String userId, int page, int size) {
        String authUserId = SecurityContextUtils.getUserId();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(User::getId)
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

        //New codes
        List<String> postedUserIdsDistinct = Stream.concat(
                fetchedPosts.stream().map(Post::getUserId),
                fetchedPosts.stream().filter(post -> post.getType().equals(EPostType.SHARE))
                        .map(post -> sharedPostMap.get(post.getSharedPostId()).getUserId())
        ).distinct().toList();

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        //End new codes

        List<PostResponse> myPosts = fetchedPosts.stream().map(post -> {
            PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, sharedPostMap, true);
            postResponse.setMine(post.getUserId().equals(authUserId));
            postResponse.setUser(postedUserMap.get(post.getUserId()));

            if(post.getType().equals(EPostType.SHARE))
                postResponse.getSharedPost()
                        .setUser(postedUserMap.get(sharedPostMap.get(post.getSharedPostId()).getUserId()));

            return postResponse;

        }).peek(post -> doPostCensoring(token, post, friendIds)).toList();

        ResDTO<PaginationResponse<PostResponse>> response = new ResDTO<>();

        PaginationResponse<PostResponse> paginationResponse = new PaginationResponse<>();

        paginationResponse.setData(myPosts);
        paginationResponse.setPage(page);
        paginationResponse.setLimit(size);
        paginationResponse.setTotalPages(fetchedPosts.getTotalPages());

        response.setData(paginationResponse);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");

        return response;
    }
}