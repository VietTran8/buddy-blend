package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.CommentResponse;
import vn.edu.tdtu.dto.response.TopReacts;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.model.Comments;
import vn.edu.tdtu.repository.CommentReactionRepository;
import vn.edu.tdtu.repository.CommentsRepository;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.DateUtils;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentResponseMapper {
    private final UserService userService;
    private final CommentsRepository commentsRepository;
    private final CommentReactionRepository commentReactionRepository;

    public CommentResponse mapToDto(String token, Comments comment) {
        CommentResponse commentResponse = mapToBaseDto(token, comment);

        commentResponse.setChildren(commentsRepository.findByParentId(comment.getId())
                .stream()
                .map(cmt -> mapToChildrenCommentDTO(token, cmt))
                .toList());

        return commentResponse;
    }

    private CommentResponse mapToChildrenCommentDTO(String token, Comments comment) {
        CommentResponse commentResponse = mapToBaseDto(token, comment);

        commentResponse.setChildren(new ArrayList<>());

        return commentResponse;
    }

    private CommentResponse mapToBaseDto(String token, Comments comment) {
        CommentResponse commentResponse = new CommentResponse();

        List<CommentReactions> commentReactions = commentReactionRepository.findByCmtId(comment.getId());
        CommentReactions reacted = commentReactionRepository.findByUserIdAndCmtId(
                        SecurityContextUtils.getUserId(),
                        comment.getId()
                )
                .orElse(null);

        commentResponse.setId(comment.getId());
        commentResponse.setUser(userService.findById(token, comment.getUserId()));
        commentResponse.setCreatedAt(DateUtils.localDateTimeToDate(comment.getCreatedAt()));
        commentResponse.setUpdatedAt(DateUtils.localDateTimeToDate(comment.getUpdatedAt()));
        commentResponse.setContent(comment.getContent());
        commentResponse.setImageUrls(comment.getImageUrls());
        commentResponse.setParentId(comment.getParentId());
        commentResponse.setNoReactions(commentReactions.size());
        commentResponse.setReacted(reacted != null ? reacted.getType() : null);
        commentResponse.setTopReacts(getTopCmtReacts(commentReactions));

        return commentResponse;
    }

    private List<TopReacts> getTopCmtReacts(List<CommentReactions> commentReactions) {
        return commentReactions.stream()
                .collect(Collectors.groupingBy(CommentReactions::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<EReactionType, Long>comparingByValue().reversed())
                .map(entry -> new TopReacts(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
