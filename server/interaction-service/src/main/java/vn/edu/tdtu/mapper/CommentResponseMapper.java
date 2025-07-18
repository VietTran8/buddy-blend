package vn.edu.tdtu.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.model.Comments;
import vn.edu.tdtu.repository.CommentReactionRepository;
import vn.edu.tdtu.repository.CommentsRepository;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.CommentDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.utils.DateUtils;
import vn.tdtu.common.utils.SecurityContextUtils;

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

    public CommentDTO mapToDto(Comments comment) {
        CommentDTO commentResponse = mapToBaseDto(comment);

        commentResponse.setChildren(commentsRepository.findByParentId(comment.getId())
                .stream()
                .map(cmt -> mapToChildrenCommentDTO(cmt))
                .toList());

        return commentResponse;
    }

    private CommentDTO mapToChildrenCommentDTO(Comments comment) {
        CommentDTO commentResponse = mapToBaseDto(comment);

        commentResponse.setChildren(new ArrayList<>());

        return commentResponse;
    }

    private CommentDTO mapToBaseDto(Comments comment) {
        CommentDTO commentResponse = new CommentDTO();

        List<CommentReactions> commentReactions = commentReactionRepository.findByCmtId(comment.getId());
        CommentReactions reacted = commentReactionRepository.findByUserIdAndCmtId(
                        SecurityContextUtils.getUserId(),
                        comment.getId()
                )
                .orElse(null);

        commentResponse.setId(comment.getId());
        commentResponse.setUser(userService.findById(comment.getUserId()));
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

    private List<ReactionDTO> getTopCmtReacts(List<CommentReactions> commentReactions) {
        return commentReactions.stream()
                .collect(Collectors.groupingBy(CommentReactions::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<EReactionType, Long>comparingByValue().reversed())
                .map(entry -> new ReactionDTO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
