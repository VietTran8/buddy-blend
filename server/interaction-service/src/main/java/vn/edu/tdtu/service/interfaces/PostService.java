package vn.edu.tdtu.service.interfaces;

import vn.tdtu.common.dto.PostDTO;

public interface PostService {
    PostDTO findById(String postId);
}
