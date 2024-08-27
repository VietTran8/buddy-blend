package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.CommentResponse;
import vn.edu.tdtu.models.Comments;
import vn.edu.tdtu.repositories.CommentsRepository;
import vn.edu.tdtu.services.UserService;
import vn.edu.tdtu.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentResponseMapper {
    private final UserService userService;
    private final CommentsRepository commentsRepository;
    public CommentResponse mapToDto(String token, Comments comment){
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setUser(userService.findById(token, comment.getUserId()));
        commentResponse.setCreatedAt(DateUtils.localDateTimeToDate(comment.getCreatedAt()));
        commentResponse.setUpdatedAt(DateUtils.localDateTimeToDate(comment.getUpdatedAt()));
        commentResponse.setContent(comment.getContent());
        commentResponse.setImageUrls(comment.getImageUrls());
        commentResponse.setParentId(comment.getParentId());
        commentResponse.setChildren(commentsRepository.findByParentId(comment.getId())
                .stream()
                .map(cmt -> mapToChildrenCommentDTO(token, cmt))
                .toList());

        return commentResponse;
    }

    private CommentResponse mapToChildrenCommentDTO(String token, Comments comment){
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setUser(userService.findById(token, comment.getUserId()));
        commentResponse.setCreatedAt(DateUtils.localDateTimeToDate(comment.getCreatedAt()));
        commentResponse.setUpdatedAt(DateUtils.localDateTimeToDate(comment.getUpdatedAt()));
        commentResponse.setContent(comment.getContent());
        commentResponse.setImageUrls(comment.getImageUrls());
        commentResponse.setParentId(comment.getParentId());
        commentResponse.setChildren(new ArrayList<>());

        return commentResponse;
    }
}
