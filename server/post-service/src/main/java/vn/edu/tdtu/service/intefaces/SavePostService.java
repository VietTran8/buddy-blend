package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.PostResponse;
import vn.edu.tdtu.dto.response.SavePostResponse;

import java.util.List;

public interface SavePostService {
    public ResDTO<SavePostResponse> handleSavePost(String postId);
    public ResDTO<List<PostResponse>> getUserSavedPost(String token);
}
