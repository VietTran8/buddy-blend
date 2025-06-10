package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.dto.PostDTO;

public interface PostService {
    public PostDTO findById(String token, String postId);
}
