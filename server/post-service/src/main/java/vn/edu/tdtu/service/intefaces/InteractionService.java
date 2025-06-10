package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.CommentDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.util.List;
import java.util.Map;

public interface InteractionService {
    public List<CommentDTO> findCommentsByPostId(String token, String postId);

    public long countCommentByPostId(String token, String postId);

    public Map<EReactionType, List<ReactionDTO>> findReactionsByPostId(String token, String postId);
}
