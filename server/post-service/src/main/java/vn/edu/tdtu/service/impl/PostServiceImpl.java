package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.*;
import vn.edu.tdtu.enums.ENotificationType;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.enums.EPrivacy;
import vn.edu.tdtu.enums.ESyncType;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.request.PostPostRequestMapper;
import vn.edu.tdtu.mapper.request.UpdatePostRequestMapper;
import vn.edu.tdtu.mapper.response.PostResponseMapper;
import vn.edu.tdtu.message.ModerateImagesMessage;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.PostShare;
import vn.edu.tdtu.model.PostTag;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.BannedWordRepository;
import vn.edu.tdtu.repository.CustomPostRepository;
import vn.edu.tdtu.repository.PostRepository;
import vn.edu.tdtu.service.intefaces.GroupService;
import vn.edu.tdtu.service.intefaces.PostService;
import vn.edu.tdtu.service.intefaces.PostShareService;
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
    private final PostShareService postShareService;
    private final BannedWordRepository bannedWordRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public Post findPostById(String postId){
        return postRepository.findById(postId).orElse(null);
    }

    public ResDTO<?> findPostRespById(String token, String postId){
        ResDTO<PostResponse> response = new ResDTO<>();

        postRepository.findById(postId)
                .ifPresentOrElse(
                        post -> {
                            response.setMessage("success");
                            response.setData(postResponseMapper.mapToDto(token, post.getId(), post, false));
                            response.setCode(HttpServletResponse.SC_OK);
                        }, () -> {
                            PostResponse postResponse = postShareService.findPostRespById(token, postId);
                            if(postResponse != null) {
                                response.setMessage("success");
                                response.setData(postResponse);
                                response.setCode(HttpServletResponse.SC_OK);
                            }else {
                                throw new RuntimeException("post not found with id: " + postId);
                            }
                        }
                );

        return response;
    }

    public ResDTO<List<PostResponse>> findPostRespByIds(String token, FindByIdsReq req){
        String userId = SecurityContextUtils.getUserId();

        ResDTO<List<PostResponse>> response = new ResDTO<>();
        List<String> ids = req.getIds();

        List<Post> foundPosts = postRepository.findByIdIn(ids);
        List<PostShare> foundSharePosts = postShareService.findByIds(ids);

        List<String> sharedPostIds = foundSharePosts.stream().map(PostShare::getSharedPostId).toList();
        Map<String, String> userIdsMap = postRepository.findAllById(sharedPostIds).stream().collect(Collectors.toMap(
                Post::getId, Post::getUserId
        ));

        List<String> postedUserIdsDistinct = Stream.concat(
                        foundPosts.stream().map(Post::getUserId),
                        Stream.concat(
                                foundSharePosts.stream().map(PostShare::getSharedUserId),
                                foundSharePosts.stream().map(post -> userIdsMap.get(post.getSharedPostId()))
                        )
                )
                .distinct()
                .toList();

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<PostResponse> posts = foundPosts.stream().map(
                post -> {
                    PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, true);
                    postResponse.setUser(postedUserMap.get(post.getUserId()));
                    postResponse.setMine(post.getUserId().equals(userId));
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
        data.put("userId", "");

        postRepository.findById(postId)
                .ifPresentOrElse(
                        post -> {
                            data.put("userId", post.getUserId());
                        }, () -> {
                            postShareService.findById(postId)
                                    .ifPresentOrElse(
                                            postShare -> {
                                                data.put("userId", postShare.getSharedUserId());
                                            }, () -> {
                                                throw new RuntimeException("post not found with id: " + postId);
                                            }
                                    );
                        }
                );
        response.setMessage("success");
        response.setData(data);
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

        Page<Post> groupPosts = postRepository.findByGroupIdOrderByCreatedAtDesc(groupId, PageRequest.of(page - 1, limit));
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

        int startIndex = (req.getPage() - 1) * req.getSize();

        ResDTO<PaginationResponse<PostResponse>> response = new ResDTO<>();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(User::getId)
                .toList();

        List<String> groupIds = groupService.getMyGroups(token)
                .stream()
                .map(GroupInfo::getId)
                .toList();

        String userId = SecurityContextUtils.getUserId();

        List<Post> fetchedPosts = customPostRepository.findNewsFeed(userId, friendIds, groupIds, req.getStartTime());
        List<PostShare> fetchedSharedPost = postShareService.findSharedPostByFriendIds(friendIds, userId, req.getStartTime());

        List<String> postIds = fetchedSharedPost.stream().map(PostShare::getSharedPostId).toList();
        Map<String, String> userIdsMap = postRepository.findAllById(postIds).stream().collect(Collectors.toMap(
                Post::getId, Post::getUserId
        ));

        //New codes
        List<String> postedUserIdsDistinct = Stream.concat(
                        fetchedPosts.stream().map(Post::getUserId),
                        Stream.concat(
                                fetchedSharedPost.stream().map(PostShare::getSharedUserId),
                                fetchedSharedPost.stream().map(post -> userIdsMap.get(post.getSharedPostId()))
                        )
                )
                .distinct()
                .toList();

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        //End new codes

        //Todo: Find shared posts then combine all of them with pagination

        List<PostResponse> posts = fetchedPosts.stream().map(
                post -> {
                    PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, true);
                    postResponse.setUser(postedUserMap.get(post.getUserId()));
                    postResponse.setMine(post.getUserId().equals(userId));
                    return postResponse;
                }
        ).filter(post -> {
            if(post.getType().equals(EPostType.GROUP)) {
                if(post.getGroupInfo().isPrivate())
                    return groupIds.contains(post.getGroupInfo().getId());

                return true;
            }

            return true;
        }).toList();

        List<PostResponse> sharedPosts = fetchedSharedPost
                .stream()
                        .filter(postShare -> {
                            if(postShare.getPrivacy().equals(EPrivacy.PRIVATE)){
                               return postShare.getSharedUserId().equals(userId);
                            }

                            return true;
                        })
                        .map(
                        postShare -> {
                            return getPostShareResponse(token, friendIds, postedUserMap, postShare, userId);
                        }
                )
                .filter(Objects::nonNull)
                .toList();

        Stream<PostResponse> combinedPosts = Stream.concat(posts.stream(), sharedPosts.stream())
                .sorted(Comparator.comparing(PostResponse::getHiddenCreatedAt).reversed());

        List<PostResponse> allPost = combinedPosts.toList();

        PaginationResponse<PostResponse> paginationResponse = new PaginationResponse<>();

        paginationResponse.setData(
                allPost.stream().skip(startIndex)
                .limit(req.getSize()).toList()
        );
        paginationResponse.setPage(page);
        paginationResponse.setLimit(size);
        paginationResponse.setTotalPages((int) Math.ceil(allPost.size() * 1.0 / req.getSize()));

        response.setData(paginationResponse);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");

        return response;
    }

    public ResDTO<?> findByContentContaining(String token, String key){
        ResDTO<List<PostResponse>> response = new ResDTO<>();
        response.setData(Stream.concat(postRepository.findByContent(key).stream().map(
                p -> postResponseMapper.mapToDto(token, p.getId(), p, false)
        ), postShareService.searchSharePost(token, key).stream()).toList());
        response.setMessage("success");
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }

    public ResDTO<?> savePost(String token, CreatePostRequest postRequest){
        Post post = postPostRequestMapper.mapToObject(postRequest);

        Set<String> bannedWords = bannedWordRepository.findAll().stream()
                .map(bannedWord -> bannedWord.getWord().toLowerCase())
                .collect(Collectors.toSet());

        String content = post.getContent().toLowerCase();

        for (String word : bannedWords) {
            if (content.contains(word)) {
                throw new IllegalArgumentException("Bài viết của bạn chứa một số từ bị cấm!");
            }
        }

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

        ModerateImagesMessage message = new ModerateImagesMessage();
        message.setImageUrls(post.getImageUrls());
        message.setPostId(post.getId());

        log.info(message.toString());

        kafkaEventPublisher.pubModerateImagesMessage(message);
        kafkaEventPublisher.pubSyncPostMessage(post, ESyncType.TYPE_CREATE);

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

        //Update share post if normal post is not exists
        updateSharePost(token, request, userId, response);

        return response;
    }

    public ResDTO<?> deletePost(String postId){
        String userId = SecurityContextUtils.getUserId();

        Post foundPost = findPostById(postId);

        ResDTO<Map<String, String>> response = new ResDTO<>();
        Map<String, String> data = new HashMap<>();

        if(foundPost != null){
            if(foundPost.getUserId().equals(userId)){
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

        deleteSharePost(postId, userId, data, response);

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

        PostShare postShare = getPostShare(request, post, foundUser);
        postShareService.save(postShare);

        if(!post.getUserId().equals(userId)){
            InteractNotification notification = getInteractNotification(request, post, foundUser, userId);
            kafkaEventPublisher.pubSharePostMessage(notification);
        }

        response.setMessage("Đã chia sẻ bài viết");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(postResponseMapper.mapToDto(token, post.getId(), post, false));

        return response;
    }

    public ResDTO<PaginationResponse<PostResponse>> findUserPosts(String token, String uId, int page, int size){
        String userId = SecurityContextUtils.getUserId();

        if(uId.isEmpty())
            return findPostsByUserId(token, userId, page, size);

        return findPostsByUserId(token, uId, page, size);
    }

    private void deleteSharePost(String postId, String userId, Map<String, String> data, ResDTO<Map<String, String>> response) {
        PostShare postShare = postShareService.findById(postId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy bài viết có id: " + postId));

        if(!postShare.getSharedUserId().equals(userId))
            throw new BadRequestException("Không thể xóa bài viết của người khác");

        postShareService.delete(postShare);

        data.put("deletedId", postShare.getId());

        response.setMessage("Đã xoá bài viết");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(data);
    }

    private void updateSharePost(String token, UpdatePostContentRequest request, String userId, ResDTO<PostResponse> response) {
        PostShare postShare = postShareService.findById(request.getId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy bài viết có id: " + request.getId()));

        if(!postShare.getSharedUserId().equals(userId))
            throw new BadRequestException("Không thể cập nhật bài viết của người khác");

        postShare.setPrivacy(request.getPrivacy());
        postShare.setStatus(request.getContent());
        postShare.setNormalizedStatus(StringUtils.toSlug(request.getContent()));

        postShareService.save(postShare);

        response.setMessage("Đã cập nhật bài viết");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(postShareService.mapToPostResponse(token, postShare));
    }

    private PostResponse getPostShareResponse(String token, List<String> friendIds, Map<String, User> postedUserMap, PostShare postShare, String userId) {
        Post foundPost = findPostById(postShare.getSharedPostId());
        String authUserId = SecurityContextUtils.getUserId();

        if(foundPost != null){
            PostResponse postResponse = postResponseMapper.mapToDto(token, postShare.getId(), foundPost, true);
            postResponse.setUser(postedUserMap.get(foundPost.getUserId()));
            postResponse.setMine(postShare.getSharedUserId().equals(authUserId));
            postResponse.setType(EPostType.SHARE);
            postResponse.setHiddenCreatedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));

            ShareInfo shareInfo = new ShareInfo();
            shareInfo.setStatus(postShare.getStatus());
            shareInfo.setSharedUser(postedUserMap.get(postShare.getSharedUserId()));
            shareInfo.setSharedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));
            shareInfo.setId(postShare.getId());
            shareInfo.setPrivacy(postShare.getPrivacy());

            postResponse.setShareInfo(shareInfo);

            if(
                    (postResponse.getPrivacy().equals(EPrivacy.PRIVATE) && !postResponse.getUser().getId().equals(authUserId)) ||
                            (postResponse.getPrivacy().equals(EPrivacy.ONLY_FRIENDS) && (!friendIds.contains(postResponse.getUser().getId()) && !postResponse.getUser().getId().equals(authUserId))) ||
                            (foundPost.getType().equals(EPostType.GROUP) && !groupService.allowFetchPost(token, postResponse.getGroupInfo().getId()))
            ){
                hideIllegalPostResponse(postResponse);
            }

            return postResponse;
        }

        return null;
    }

    private void hideIllegalPostResponse(PostResponse postResponse) {
        postResponse.setVideoUrls(null);
        postResponse.setImageUrls(null);
        postResponse.setId(null);
        postResponse.setContent(null);
        postResponse.setTaggedUsers(null);
        postResponse.setIllegal(true);
    }

    private static PostShare getPostShare(SharePostRequest request, Post post, User foundUser) {
        PostShare postShare = new PostShare();

        postShare.setPrivacy(request.getPrivacy());
        postShare.setSharedAt(LocalDateTime.now());
        postShare.setSharedUserId(foundUser.getId());
        postShare.setStatus(request.getStatus());
        postShare.setSharedPostId(post.getId());
        postShare.setNormalizedStatus(StringUtils.toSlug(request.getStatus()));

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

    private ResDTO<PaginationResponse<PostResponse>> findPostsByUserId(String token, String userId, int page, int size) {
        String authUserId = SecurityContextUtils.getUserId();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(User::getId)
                .toList();

        List<Post> fetchedPosts = postRepository.findByUserIdOrPostTagsTaggedUserIdWithPrivacy(userId, userId, friendIds, SecurityContextUtils.getUserId());
        List<PostShare> fetchedSharedPost = postShareService.findSharedPostByUserId(userId);

        List<String> postIds = fetchedSharedPost.stream().map(PostShare::getSharedPostId).toList();
        Map<String, String> userIdsMap = postRepository.findAllById(postIds).stream().collect(Collectors.toMap(
                Post::getId, Post::getUserId
        ));

        //New codes
        List<String> postedUserIdsDistinct = Stream.concat(
                        fetchedPosts.stream().map(Post::getUserId),
                        Stream.concat(
                                fetchedSharedPost.stream().map(PostShare::getSharedUserId),
                                fetchedSharedPost.stream().map(post -> userIdsMap.get(post.getSharedPostId()))
                        )
                )
                .distinct()
                .toList();

        Map<String, User> postedUserMap = userService.findByIds(token, postedUserIdsDistinct)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        //End new codes

        List<PostResponse> myPosts = fetchedPosts.stream().map(post -> {
            PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post, true);
            postResponse.setMine(post.getUserId().equals(authUserId));
            postResponse.setUser(postedUserMap.get(post.getUserId()));

            return postResponse;

        }).toList();

        //TODO: Find my share post

        List<PostResponse> sharedPosts = fetchedSharedPost
                .stream()
                .filter(postShare -> {
                    if(postShare.getPrivacy().equals(EPrivacy.PRIVATE)){
                        return postShare.getSharedUserId().equals(authUserId);
                    }

                    return true;
                }).map(
                        postShare -> {
                            return getPostShareResponse(token, friendIds, postedUserMap, postShare, userId);
                        }
                )
                .filter(Objects::nonNull)
                .toList();

        List<PostResponse> allPost = Stream.concat(myPosts.stream(), sharedPosts.stream())
                .sorted(Comparator.comparing(PostResponse::getHiddenCreatedAt).reversed())
                .toList();

        ResDTO<PaginationResponse<PostResponse>> response = new ResDTO<>();

        PaginationResponse<PostResponse> paginationResponse = new PaginationResponse<>();

        int startIndex = (page - 1) * size;

        paginationResponse.setData(
                allPost.stream().skip(startIndex)
                        .limit(size).toList()
        );
        paginationResponse.setPage(page);
        paginationResponse.setLimit(size);
        paginationResponse.setTotalPages((int) Math.ceil(allPost.size() * 1.0 / size));

        response.setData(paginationResponse);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");

        return response;
    }
}