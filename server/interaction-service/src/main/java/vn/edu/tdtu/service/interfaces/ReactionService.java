package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;
import java.util.Map;

public interface ReactionService {
    ResponseVM<?> doReaction(DoReactRequest request);

    ResponseVM<Map<EReactionType, List<ReactionDTO>>> getReactsByPostId(String postId);
}
