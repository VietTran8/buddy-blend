package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.util.List;
import java.util.Map;

public interface ReactionService {
    public ResDTO<?> doReaction(String token, DoReactRequest request);

    public ResDTO<Map<EReactionType, List<ReactionDTO>>> getReactsByPostId(String token, String postId);
}
