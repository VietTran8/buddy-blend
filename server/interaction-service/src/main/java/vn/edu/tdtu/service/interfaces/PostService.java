package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.Post;

public interface PostService {
    public Post findById(String token, String postId);
}
