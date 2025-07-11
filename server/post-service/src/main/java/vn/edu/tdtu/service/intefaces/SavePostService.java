package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.response.SavePostResponse;
import vn.tdtu.common.dto.PostDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

public interface SavePostService {
    ResponseVM<SavePostResponse> handleSavePost(String postId);

    ResponseVM<List<PostDTO>> getUserSavedPost(String token);
}
