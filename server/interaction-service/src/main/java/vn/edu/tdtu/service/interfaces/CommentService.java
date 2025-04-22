package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.AddCommentRequest;
import vn.edu.tdtu.dto.requests.UpdateCommentRequest;

public interface CommentService {
    public ResDTO<?> addComment(String token, AddCommentRequest request);

    public ResDTO<?> countCommentByPostId(String postId);

    public ResDTO<?> updateComment(String token, String id, UpdateCommentRequest comment);

    public ResDTO<?> deleteComment(String id);

    public ResDTO<?> findCommentById(String token, String id);

    public ResDTO<?> findCommentsByPostId(String token, String postId);

    public ResDTO<?> findAllComments(String token);
}
