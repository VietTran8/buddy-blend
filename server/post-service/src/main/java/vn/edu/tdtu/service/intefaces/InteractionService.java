package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.dto.CommentDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.util.List;
import java.util.Map;

public interface InteractionService {
    List<CommentDTO> findCommentsByPostId(String postId);

    long countCommentByPostId(String postId);

    Map<EReactionType, List<ReactionDTO>> findReactionsByPostId(String postId);
}
