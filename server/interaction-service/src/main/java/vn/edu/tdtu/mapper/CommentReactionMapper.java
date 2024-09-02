package vn.edu.tdtu.mapper;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.requests.DoCommentReactRequest;
import vn.edu.tdtu.models.CommentReactions;

import java.time.LocalDateTime;

@Component
public class CommentReactionMapper {
    public CommentReactions mapToObject(String userId, DoCommentReactRequest dto){
        CommentReactions commentReactions = new CommentReactions();
        commentReactions.setType(dto.getType());
        commentReactions.setUserId(userId);
        commentReactions.setCmtId(dto.getCmtId());
        commentReactions.setCreatedAt(LocalDateTime.now());

        return commentReactions;
    }
}
