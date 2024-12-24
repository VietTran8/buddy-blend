package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.model.Comment;
import vn.edu.tdtu.model.Reacts;

import java.util.List;
import java.util.Map;

public interface InteractionService {
    public List<Comment> findCommentsByPostId(String token , String postId);
    public long countCommentByPostId(String token ,String postId);
    public Map<EReactionType, List<Reacts>> findReactionsByPostId(String token, String postId);
}
