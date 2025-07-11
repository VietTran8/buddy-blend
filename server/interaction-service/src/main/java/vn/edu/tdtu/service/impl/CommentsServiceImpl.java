package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.requests.AddCommentRequest;
import vn.edu.tdtu.dto.requests.UpdateCommentRequest;
import vn.edu.tdtu.dto.response.InteractNotification;
import vn.edu.tdtu.mapper.CommentResponseMapper;
import vn.edu.tdtu.model.Comments;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.CommentsRepository;
import vn.edu.tdtu.service.interfaces.CommentService;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.CommentDTO;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.notification.ENotificationType;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsServiceImpl implements CommentService {
    private final CommentsRepository commentsRepository;
    private final CommentResponseMapper commentResponseMapper;
    private final KafkaEventPublisher kafkaMsgService;
    private final UserService userService;
    private final PostService postService;

    @Override
    public ResponseVM<?> addComment(String token, AddCommentRequest request) {
        String postId = request.getPostId();
        String parentCommentId = request.getParentId();
        String userIdFromJwtToken = SecurityContextUtils.getUserId();

        UserDTO foundUser = userService.findById(token, userIdFromJwtToken);

        if (foundUser == null)
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND_ID, userIdFromJwtToken);

        PostDTO foundPost = postService.findById(token, request.getPostId());
        if (foundPost == null)
            throw new BadRequestException(MessageCode.Post.POST_NOT_FOUND_ID, request.getPostId());

        Comments comment = new Comments();
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setUserId(userIdFromJwtToken);
        comment.setContent(request.getContent());
        comment.setImageUrls(request.getImageUrls());
        comment.setPostId(postId);

        if (parentCommentId != null && commentsRepository.existsById(parentCommentId)) {
            comment.setParentId(request.getParentId());
        } else if (parentCommentId != null && !commentsRepository.existsById(parentCommentId)) {
            throw new BadRequestException(MessageCode.Interaction.COMMENT_NOT_FOUND_ID, request.getParentId());
        }

        Comments savedComment = commentsRepository.save(comment);
        CommentDTO commentResponse = commentResponseMapper.mapToDto(token, savedComment);
        commentResponse.setMine(true);

        //Send message if user comment on the post directly

        log.info("comment user id: " + foundUser.getId());
        log.info("posted user id: " + foundPost.getUser().getId());
        log.info("result: " + !foundUser.getId().equals(foundPost.getUser().getId()));

        if (parentCommentId == null && !foundUser.getId().equals(foundPost.getUser().getId())) {
            InteractNotification interactNotification = new InteractNotification();
            interactNotification.setAvatarUrl(foundUser.getProfilePicture());
            interactNotification.setUserFullName(String.join(" ", foundUser.getFirstName(), foundUser.getMiddleName(), foundUser.getLastName()));
            interactNotification.setContent("đã bình luận về bài viết của bạn");
            interactNotification.setRefId(postId);
            interactNotification.setTitle("Có người tương tác nè!");
            interactNotification.setFromUserId(userIdFromJwtToken);
            interactNotification.setToUserIds(List.of(foundPost.getUser().getId()));
            interactNotification.setType(ENotificationType.COMMENT);
            interactNotification.setCreateAt(new Date().getTime() + "");

            kafkaMsgService.publishInteractNoti(interactNotification);
        }

        return new ResponseVM<>(MessageCode.Interaction.COMMENT_CREATED, commentResponse, 200);
    }

    @Override
    public ResponseVM<?> countCommentByPostId(String postId) {
        return new ResponseVM<>(
                MessageCode.Interaction.COMMENT_FETCHED,
                commentsRepository.countByPostId(postId),
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> updateComment(String token, String id, UpdateCommentRequest comment) {
        String userId = SecurityContextUtils.getUserId();

        CommentDTO updatedComment = commentsRepository.findById(id)
                .map(existingComment -> {
                    if (existingComment.getUserId().equals(userId)) {
                        existingComment.setContent(comment.getContent());
                        existingComment.setImageUrls(comment.getImageUrls());
                        existingComment.setUpdatedAt(LocalDateTime.now());

                        CommentDTO commentResponse = commentResponseMapper.mapToDto(token, commentsRepository.save(existingComment));
                        commentResponse.setMine(true);

                        return commentResponse;
                    } else {
                        throw new RuntimeException("You cannot update other people's comments");
                    }

                }).orElseThrow(() -> new RuntimeException("Comment not found with id " + id));

        return new ResponseVM<>(MessageCode.Interaction.COMMENT_UPDATED, updatedComment, 200);
    }

    @Override
    public ResponseVM<?> deleteComment(String id) {
        String userId = SecurityContextUtils.getUserId();
        ResponseVM<?> response = new ResponseVM<>();
        commentsRepository.findById(id).ifPresentOrElse(
                cmt -> {
                    if (userId.equals(cmt.getUserId())) {
                        response.setMessage(MessageCode.Interaction.COMMENT_DELETED);
                        response.setCode(200);
                        response.setData(null);

                        commentsRepository.deleteById(id);
                    } else {
                        response.setMessage(MessageCode.Interaction.COMMENT_CAN_NOT_DELETE_OTHERS);
                        response.setCode(400);
                        response.setData(null);
                    }
                }, () -> {
                    response.setMessage(MessageCode.Interaction.COMMENT_NOT_FOUND_ID, id);
                    response.setCode(400);
                    response.setData(null);
                }
        );
        return response;
    }

    @Override
    public ResponseVM<?> findCommentById(String token, String id) {
        String userId = SecurityContextUtils.getUserId();

        CommentDTO comment = commentsRepository.findById(id)
                .map(cmt -> commentResponseMapper.mapToDto(token, cmt))
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + id));

        comment.setMine(comment.getUser().getId().equals(userId));
        comment.getChildren().forEach(cmt -> {
            cmt.setMine(cmt.getUser().getId().equals(userId));
        });
        return new ResponseVM<>(MessageCode.Interaction.COMMENT_FETCHED, comment, 200);
    }

    @Override
    public ResponseVM<?> findCommentsByPostId(String token, String postId) {
        String userId = SecurityContextUtils.getUserId();

        List<Comments> comments = commentsRepository.findByPostIdAndParentIdIsNull(postId);
        List<CommentDTO> commentResponses = comments.stream().map(
                comment -> {
                    CommentDTO response = commentResponseMapper.mapToDto(token, comment);
                    response.setMine(response.getUser().getId().equals(userId));
                    response.getChildren().forEach(cmt -> {
                        cmt.setMine(cmt.getUser().getId().equals(userId));
                    });
                    return response;
                }
        ).sorted(
                (cmt1, cmt2) -> cmt2.getCreatedAt().compareTo(cmt1.getCreatedAt())
        ).toList();
        return new ResponseVM<>(MessageCode.Interaction.COMMENT_FETCHED, commentResponses, 200);
    }

    @Override
    public ResponseVM<?> findAllComments(String token) {
        String userId = SecurityContextUtils.getUserId();
        List<CommentDTO> commentResponses = commentsRepository.findAll().stream().map(
                comment -> {
                    CommentDTO response = commentResponseMapper.mapToDto(token, comment);
                    response.setMine(response.getUser().getId().equals(userId));
                    response.getChildren().forEach(cmt -> {
                        cmt.setMine(cmt.getUser().getId().equals(userId));
                    });
                    return response;
                }
        ).toList();
        return new ResponseVM<>(MessageCode.Interaction.COMMENT_FETCHED, commentResponses, 200);
    }
}
