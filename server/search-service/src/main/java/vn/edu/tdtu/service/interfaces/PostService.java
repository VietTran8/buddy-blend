package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.model.data.Post;
import vn.edu.tdtu.model.es.SyncPost;

import java.util.List;

public interface PostService {
    void savePost(SyncPost post);
    void updatePost(SyncPost post);
    void deletePost(SyncPost post);
    List<Post> findByContentContaining(String token, String key, String fuzziness);
}
