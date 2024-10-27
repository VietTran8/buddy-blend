package vn.edu.tdtu.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.*;
import vn.edu.tdtu.dtos.response.*;
import vn.edu.tdtu.enums.*;
import vn.edu.tdtu.mappers.request.PostPostRequestMapper;
import vn.edu.tdtu.mappers.request.UpdatePostRequestMapper;
import vn.edu.tdtu.mappers.response.PostResponseMapper;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.models.PostShare;
import vn.edu.tdtu.models.PostTag;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.BannedWordRepository;
import vn.edu.tdtu.repositories.CustomPostRepository;
import vn.edu.tdtu.repositories.PostRepository;
import vn.edu.tdtu.repositories.ReportRepository;
import vn.edu.tdtu.utils.DateUtils;
import vn.edu.tdtu.utils.JwtUtils;
import vn.edu.tdtu.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostResponseMapper postResponseMapper;
    private final PostPostRequestMapper postPostRequestMapper;
    private final UpdatePostRequestMapper updatePostRequestMapper;
    private final CustomPostRepository customPostRepository;
    private final JwtUtils jwtUtils;
    private final FileService fileService;
    private final UserService userService;
    private final GroupService groupService;
    private final ReportRepository reportRepository;
    private final PostShareService postShareService;
    private final BannedWordRepository bannedWordRepository;
    private final SendKafkaMsgService kafkaMsgService;
    private final ModerationService moderationService;

    public Post findPostById(String postId){
        return postRepository.findById(postId).orElse(null);
    }

    public ResDTO<?> findPostRespById(String token, String postId){
        ResDTO<PostResponse> response = new ResDTO<>();

        postRepository.findById(postId)
                .ifPresentOrElse(
                        post -> {
                            response.setMessage("success");
                            response.setData(postResponseMapper.mapToDto(token, post.getId(), post));
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

    public ResDTO<?> findPostRespByIds(String token, FindByIdsReq req){
        ResDTO<List<PostResponse>> response = new ResDTO<>();
        List<PostResponse> postResponses = new ArrayList<>();

        req.getIds().forEach(postId -> {
            postRepository.findById(postId)
                    .ifPresentOrElse(
                            post -> {
                                postResponses.add(postResponseMapper.mapToDto(token, post.getId(), post));
                            }, () -> {
                                PostResponse postResponse = postShareService.findPostRespById(token, postId);
                                if(postResponse != null) {
                                    postResponses.add(postResponse);
                                }
                            }
                    );
        });

        response.setCode(200);
        response.setData(postResponses);
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
        return postResponseMapper.mapToDto(token, post.getId(), post);
    }

    public ResDTO<?> getGroupPosts(String token, String groupId, int page, int limit) {
        ResDTO<PaginationResponse<PostResponse>> response = new ResDTO<>();
        response.setMessage("Group posts fetched successfully");
        response.setCode(HttpServletResponse.SC_OK);

        Page<Post> groupPosts = postRepository.findByGroupId(groupId, PageRequest.of(page, limit));

        String userId = jwtUtils.getUserIdFromJwtToken(token);

        response.setData(new PaginationResponse<>(
                page,
                limit,
                groupPosts.getTotalPages(),
                groupPosts.stream().map(
                        post -> {
                            PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post);
                            postResponse.setMine(post.getUserId().equals(userId));

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

        ResDTO<Map<String, Object>> response = new ResDTO<>();

        List<String> friendIds = userService.findUserFriendIdsByUserToken(token)
                .stream()
                .map(User::getId)
                .toList();

        List<String> groupIds = groupService.getMyGroups(token)
                .stream()
                .map(GroupInfo::getId)
                .toList();;

        String userId = jwtUtils.getUserIdFromJwtToken(token);

        List<PostResponse> posts = customPostRepository.findNewsFeed(userId, friendIds, groupIds, req.getStartTime())
                .stream().map(
                        post -> {
                            PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post);
                            postResponse.setMine(post.getUserId().equals(userId));
                            return postResponse;
                        }
                ).toList();

        //Todo: Find shared posts then combine all of them with pagination

        List<PostResponse> sharedPosts = postShareService.findSharedPostByFriendIds(friendIds, userId, req.getStartTime())
                .stream()
                        .filter(postShare -> {
                            if(postShare.getPrivacy().equals(EPrivacy.PRIVATE)){
                               return postShare.getSharedUserId().equals(userId);
                            }

                            return true;
                        })
                        .map(
                        postShare -> {
                            Post foundPost = findPostById(postShare.getSharedPostId());
                            if(foundPost != null){
                                PostResponse postResponse = postResponseMapper.mapToDto(token, postShare.getId(), foundPost);
                                postResponse.setMine(foundPost.getUserId().equals(userId));
                                postResponse.setType(EPostType.SHARE);

                                ShareInfo shareInfo = new ShareInfo();
                                shareInfo.setStatus(postShare.getStatus());
                                shareInfo.setSharedUser(userService.findById(token, postShare.getSharedUserId()));
                                shareInfo.setSharedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));
                                shareInfo.setId(postShare.getId());
                                shareInfo.setPrivacy(postShare.getPrivacy());

                                postResponse.setShareInfo(shareInfo);
                                postResponse.setCreatedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));

                                return postResponse;
                            }
                            return null;
                        }
                )
                .filter(Objects::nonNull)
                .toList();

        Stream<PostResponse> combinedPosts = Stream.concat(posts.stream(), sharedPosts.stream())
                .sorted(Comparator.comparing(PostResponse::getCreatedAt).reversed());

        List<PostResponse> allPost = combinedPosts.toList();

        Map<String, Object> data = new HashMap<>();

        data.put("totalPages", allPost.size() / req.getSize());
        data.put("posts", allPost.stream().skip(startIndex)
                .limit(req.getSize()).toList());

        response.setData(data);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("success");

        return response;
    }

    public ResDTO<?> findByContentContaining(String token, String key){
        ResDTO<List<PostResponse>> response = new ResDTO<>();
        response.setData(Stream.concat(postRepository.findByContent(key).stream().map(
                p -> postResponseMapper.mapToDto(token, p.getId(), p)
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

        post.getImageUrls().forEach(img -> {
            if(!moderationService.moderateImage(img)){
                fileService.delete(img, EFileType.TYPE_IMG);
                throw new IllegalArgumentException("Bài viết của bạn chứa một số chứa hình ảnh không phù hợp!");
            }
        });

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

        post.setUserId(jwtUtils.getUserIdFromJwtToken(token));
        post = postRepository.save(post);

        PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post);
        postResponse.setMine(post.getUserId().equals(jwtUtils.getUserIdFromJwtToken(token)));

        ResDTO<PostResponse> response = new ResDTO<>();
        response.setMessage("Đã đăng bài viết");
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(postResponse);

        kafkaMsgService.pubSyncPostMessage(post, ESyncType.TYPE_CREATE);

        return response;
    }

    public ResDTO<?> updatePostContent(String token, UpdatePostContentRequest request){
        String userId = jwtUtils.getUserIdFromJwtToken(token);

        Post foundPost = findPostById(request.getId());
        ResDTO<PostResponse> response = new ResDTO<>();

        if(foundPost != null){
            if(foundPost.getUserId().equals(userId)){
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
                response.setData(postResponseMapper.mapToDto(token, foundPost.getId(), foundPost));

                kafkaMsgService.pubSyncPostMessage(foundPost, ESyncType.TYPE_UPDATE);

                return response;
            }
            response.setMessage("Không thể cập nhật bài viết của người khác");
            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
            response.setData(null);

            return response;
        }

        //Update share post if normal post is not exists
        updateSharePost(token, request, userId, response);

        return response;
    }

    private void updateSharePost(String token, UpdatePostContentRequest request, String userId, ResDTO<PostResponse> response) {
        postShareService.findById(request.getId()).ifPresentOrElse(
                postShare -> {
                    if(postShare.getSharedUserId().equals(userId)){
                        postShare.setPrivacy(request.getPrivacy());
                        postShare.setStatus(request.getContent());
                        postShare.setNormalizedStatus(StringUtils.toSlug(request.getContent()));

                        postShareService.save(postShare);

                        response.setMessage("Đã cập nhật bài viết");
                        response.setCode(HttpServletResponse.SC_OK);
                        response.setData(postShareService.mapToPostResponse(token, postShare));
                    }else {
                        response.setMessage("Không thể cập nhật bài viết của người khác");
                        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                        response.setData(null);
                    }
                },
                () -> {
                    response.setMessage("Không tìm thấy bài viết có id: " + request.getId());
                    response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                    response.setData(null);
                }
        );
    }

    public ResDTO<?> deletePost(String token, String postId){
        String userId = jwtUtils.getUserIdFromJwtToken(token);

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

                kafkaMsgService.pubSyncPostMessage(foundPost, ESyncType.TYPE_DELETE);

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

    private void deleteSharePost(String postId, String userId, Map<String, String> data, ResDTO<Map<String, String>> response) {
        postShareService.findById(postId).ifPresentOrElse(
                postShare -> {
                    if(postShare.getSharedUserId().equals(userId)){
                        postShareService.delete(postShare);

                        data.put("deletedId", postShare.getId());

                        response.setMessage("Đã xoá bài viết");
                        response.setCode(HttpServletResponse.SC_OK);
                        response.setData(data);
                    } else {
                        response.setMessage("Không thể xóa bài viết của người khác");
                        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                        response.setData(null);
                    }
                },
                () -> {
                    response.setMessage("Không tìm thấy bài viết có id: " + postId);
                    response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                    response.setData(null);
                }
        );
    }

    public ResDTO<?> sharePost(String token, SharePostRequest request){
        ResDTO<PostResponse> response = new ResDTO<>();

        postRepository.findById(request.getPostId())
                .ifPresentOrElse(
                        (post) -> {
                            String userId = jwtUtils.getUserIdFromJwtToken(token);
                            User foundUser = userService.findById(token, userId);

                            if(foundUser != null) {
                                PostShare postShare = getPostShare(request, post, foundUser);
                                postShareService.save(postShare);

                                InteractNotification notification = getInteractNotification(request, post, foundUser, userId);
                                kafkaMsgService.pubSharePostMessage(notification);

                                response.setMessage("Đã chia sẻ bài viết");
                                response.setCode(HttpServletResponse.SC_OK);
                                response.setData(postResponseMapper.mapToDto(token, post.getId(), post));
                            }else{
                                response.setMessage("Không tìm thấy người dùng");
                                response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                                response.setData(null);
                            }

                        }, () -> {
                            response.setMessage("Không tìm thấy bài viết");
                            response.setCode(HttpServletResponse.SC_BAD_REQUEST);
                            response.setData(null);
                        }
                );

        return response;
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
        notification.setPostId(request.getPostId());
        notification.setTitle("Có người tương tác nè!");
        notification.setFromUserId(userId);
        notification.setToUserId(post.getUserId());
        notification.setType(ENotificationType.SHARE);
        notification.setCreateAt(new Date());

        return notification;
    }

    public ResDTO<?> findUserPosts(String token, String uId){
        String userId = jwtUtils.getUserIdFromJwtToken(token);

        if(uId.isEmpty())
            return findPostsByUserId(token, userId);

        return findPostsByUserId(token, uId);
    }

    private ResDTO<List<PostResponse>> findPostsByUserId(String token, String userId) {
        List<Post> posts = postRepository.findByUserIdOrPostTagsTaggedUserId(userId, userId);

        List<PostResponse> myPosts = posts.stream().map(post -> {
            PostResponse postResponse = postResponseMapper.mapToDto(token, post.getId(), post);
            postResponse.setMine(post.getUserId().equals(userId));

            return postResponse;

        }).toList();

        //TODO: Find my share post

        List<PostResponse> sharedPosts = postShareService.findSharedPostByUserId(userId)
                .stream().map(
                        postShare -> {
                            Post foundPost = findPostById(postShare.getSharedPostId());
                            if(foundPost != null){
                                PostResponse postResponse = postResponseMapper.mapToDto(token, postShare.getId(), foundPost);
                                postResponse.setMine(foundPost.getUserId().equals(userId));
                                postResponse.setType(EPostType.SHARE);
                                postResponse.setCreatedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));

                                ShareInfo shareInfo = new ShareInfo();
                                shareInfo.setStatus(postShare.getStatus());
                                shareInfo.setSharedUser(userService.findById(token, postShare.getSharedUserId()));
                                shareInfo.setSharedAt(DateUtils.localDateTimeToDate(postShare.getSharedAt()));
                                shareInfo.setId(postShare.getId());
                                shareInfo.setPrivacy(postShare.getPrivacy());

                                postResponse.setShareInfo(shareInfo);

                                return postResponse;
                            }
                            return null;
                        }
                )
                .filter(p -> p != null && p.getPrivacy().equals(EPrivacy.PUBLIC))
                .toList();

        List<PostResponse> combinedPosts = Stream.concat(myPosts.stream(), sharedPosts.stream())
                .sorted(Comparator.comparing(PostResponse::getCreatedAt).reversed())
                .toList();

        ResDTO<List<PostResponse>> response = new ResDTO<>();
        response.setData(combinedPosts);

        response.setMessage("success");
        response.setCode(HttpServletResponse.SC_OK);

        return response;
    }
}