package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;
import java.util.Map;

public interface CommentReactionService {
    ResponseVM<?> doReact(DoCommentReactRequest request);

    ResponseVM<Map<EReactionType, List<ReactionDTO>>> getReactsByCmtId(String token, String cmtId);
}
