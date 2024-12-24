package vn.edu.tdtu.mapper;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.model.CommentReactions;

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
