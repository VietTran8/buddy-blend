package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoReactRequest;
import vn.edu.tdtu.dto.response.ReactResponse;
import vn.edu.tdtu.enums.EReactionType;

import java.util.List;
import java.util.Map;

public interface ReactionService {
    public ResDTO<?> doReaction(String token, DoReactRequest request);

    public ResDTO<Map<EReactionType, List<ReactResponse>>> getReactsByPostId(String token, String postId);
}
