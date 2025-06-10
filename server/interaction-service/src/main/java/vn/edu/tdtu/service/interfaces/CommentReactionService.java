package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.util.List;
import java.util.Map;

public interface CommentReactionService {
    public ResDTO<?> doReact(DoCommentReactRequest request);

    public ResDTO<Map<EReactionType, List<ReactionDTO>>> getReactsByCmtId(String token, String cmtId);
}
