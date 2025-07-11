package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.requests.AddCommentRequest;
import vn.edu.tdtu.dto.requests.UpdateCommentRequest;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface CommentService {
    ResponseVM<?> addComment(String token, AddCommentRequest request);

    ResponseVM<?> countCommentByPostId(String postId);

    ResponseVM<?> updateComment(String token, String id, UpdateCommentRequest comment);

    ResponseVM<?> deleteComment(String id);

    ResponseVM<?> findCommentById(String token, String id);

    ResponseVM<?> findCommentsByPostId(String token, String postId);

    ResponseVM<?> findAllComments(String token);
}