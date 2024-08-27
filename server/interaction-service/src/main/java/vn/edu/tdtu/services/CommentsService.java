package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.requests.AddCommentRequest;
import vn.edu.tdtu.dtos.requests.UpdateCommentRequest;
import vn.edu.tdtu.dtos.response.CommentResponse;
import vn.edu.tdtu.dtos.response.InteractNotification;
import vn.edu.tdtu.enums.ENotificationType;
import vn.edu.tdtu.enums.EPostType;
import vn.edu.tdtu.mapper.CommentResponseMapper;
import vn.edu.tdtu.models.Comments;
import vn.edu.tdtu.models.Post;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repositories.CommentsRepository;
import vn.edu.tdtu.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final JwtUtils jwtUtils;
    private final CommentResponseMapper commentResponseMapper;
    private final SendKafkaMsgService kafkaMsgService;
    private final UserService userService;
    private final PostService postService;

    public ResDTO<?> addComment(String token, AddCommentRequest request) {
        String postId = request.getPostId();
        String parentCommentId = request.getParentId();
        String userIdFromJwtToken = jwtUtils.getUserIdFromJwtToken(token);

        User foundUser = userService.findById(token, userIdFromJwtToken);

        if(foundUser != null){
            Post foundPost = postService.findById(token, request.getPostId());
            if(foundPost != null){
                if(foundPost.getType().equals(EPostType.SHARE))
                    postId = foundPost.getShareInfo().getId();

                Comments comment = new Comments();
                comment.setCreatedAt(LocalDateTime.now());
                comment.setUpdatedAt(LocalDateTime.now());
                comment.setUserId(userIdFromJwtToken);
                comment.setContent(request.getContent());
                comment.setImageUrls(request.getImageUrls());
                comment.setPostId(postId);

                if(parentCommentId != null && commentsRepository.existsById(parentCommentId)){
                    comment.setParentId(request.getParentId());
                }else if(parentCommentId != null && !commentsRepository.existsById(parentCommentId)){
                    throw new RuntimeException("Parent comment not found with id: " + request.getParentId());
                }

                Comments savedComment = commentsRepository.save(comment);
                CommentResponse commentResponse = commentResponseMapper.mapToDto(token, savedComment);
                commentResponse.setMine(true);

                //Send message if user comment on the post directly

                log.info("comment user id: " + foundUser.getId());
                log.info("posted user id: " + foundPost.getUser().getId());
                log.info("result: " + !foundUser.getId().equals(foundPost.getUser().getId()));


                if(parentCommentId == null && !foundUser.getId().equals(foundPost.getUser().getId())){
                    InteractNotification interactNotification = new InteractNotification();
                    interactNotification.setAvatarUrl(foundUser.getProfilePicture());
                    interactNotification.setUserFullName(String.join(" ", foundUser.getFirstName(), foundUser.getMiddleName(), foundUser.getLastName()));
                    interactNotification.setContent(interactNotification.getUserFullName() + " đã bình luận về bài viết của bạn");
                    interactNotification.setPostId(postId);
                    interactNotification.setTitle("Có người tương tác nè!");
                    interactNotification.setFromUserId(userIdFromJwtToken);
                    interactNotification.setToUserId(foundPost.getUser().getId());
                    interactNotification.setType(ENotificationType.COMMENT);
                    interactNotification.setCreateAt(new Date().getTime() + "");

                    kafkaMsgService.publishInteractNoti(interactNotification);
                }

                return new ResDTO<>("Comment added successfully", commentResponse, 200);
            }

            throw new RuntimeException("Post not found with id: " + request.getPostId());
        }
        throw new RuntimeException("User not found with id: " + userIdFromJwtToken);
    }


    public ResDTO<?> updateComment(String token, String id, UpdateCommentRequest comment) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);

        CommentResponse updatedComment = commentsRepository.findById(id)
                .map(existingComment -> {
                    if(existingComment.getUserId().equals(userId)){
                        existingComment.setContent(comment.getContent());
                        existingComment.setImageUrls(comment.getImageUrls());
                        existingComment.setUpdatedAt(LocalDateTime.now());

                        CommentResponse commentResponse = commentResponseMapper.mapToDto(token, commentsRepository.save(existingComment));
                        commentResponse.setMine(true);

                        return commentResponse;
                    }else{
                        throw new RuntimeException("You cannot update other people's comments");
                    }

                }).orElseThrow(() -> new RuntimeException("Comment not found with id " + id));

        return new ResDTO<>("Comment updated successfully", updatedComment, 200);
    }


    public ResDTO<?> deleteComment(String token, String id) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        ResDTO<?> response = new ResDTO<>();
        commentsRepository.findById(id).ifPresentOrElse(
                cmt -> {
                    if(userId.equals(cmt.getUserId())){
                        response.setMessage("Comment deleted successfully");
                        response.setCode(200);
                        response.setData(null);

                        commentsRepository.deleteById(id);
                    }else{
                        response.setMessage("Comments made by others cannot be deleted");
                        response.setCode(400);
                        response.setData(null);
                    }
                }, () -> {
                    response.setMessage("Comment not fount with id: " + id);
                    response.setCode(400);
                    response.setData(null);
                }
        );
        return response;
    }


    public ResDTO<?> findCommentById(String token, String id) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);

        CommentResponse comment = commentsRepository.findById(id)
                .map(cmt -> commentResponseMapper.mapToDto(token, cmt))
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + id));

        comment.setMine(comment.getUser().getId().equals(userId));
        comment.getChildren().forEach(cmt -> {
            cmt.setMine(cmt.getUser().getId().equals(userId));
        });
        return new ResDTO<>("Comment found successfully", comment, 200);
    }


    public ResDTO<?> findCommentsByPostId(String token, String postId) {
        String userId;
        if(token != null && !token.isEmpty()){
           userId = jwtUtils.getUserIdFromJwtToken(token);
        }else{
            userId = "";
        }

        List<Comments> comments = commentsRepository.findByPostIdAndParentIdIsNull(postId);
        List<CommentResponse> commentResponses = comments.stream().map(
                comment -> {
                    CommentResponse response = commentResponseMapper.mapToDto(token, comment);
                    response.setMine(response.getUser().getId().equals(userId));
                    response.getChildren().forEach(cmt -> {
                        cmt.setMine(cmt.getUser().getId().equals(userId));
                    });
                    return response;
                }
        ).sorted(
                (cmt1, cmt2) -> cmt2.getCreatedAt().compareTo(cmt1.getCreatedAt())
        ).toList();
        return new ResDTO<>("Comments retrieved successfully", commentResponses, 200);
    }

    public ResDTO<?> findAllComments(String token) {
        String userId = jwtUtils.getUserIdFromJwtToken(token);
        List<CommentResponse> commentResponses = commentsRepository.findAll().stream().map(
                comment -> {
                    CommentResponse response = commentResponseMapper.mapToDto(token, comment);
                    response.setMine(response.getUser().getId().equals(userId));
                    response.getChildren().forEach(cmt -> {
                        cmt.setMine(cmt.getUser().getId().equals(userId));
                    });
                    return response;
                }
        ).toList();
        return new ResDTO<>("Comments retrieved successfully", commentResponses, 200);
    }
}
